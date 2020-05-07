package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.module.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Collections.unmodifiableCollection;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private final Module module;
    private final ModuleChangeNotifier publisher;

    // MODULES
    private ModuleDTO flowControlModule; // system components module (loaded only once)
    private ModuleDTO currentModule; // current custom components module being developed (refreshed when compiled)
    private final List<ModuleDTO> mavenModules = new ArrayList<>(); // components from Maven dependencies (refreshed when Maven import finished)

    private ComponentTracker componentTracker;
    private CompletionTracker completionTracker;

    public ComponentServiceImpl(Project project, Module module) {
        MessageBus messageBus = project.getMessageBus();
        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        this.module = module;
        this.publisher = messageBus.syncPublisher(Topics.COMPONENTS_UPDATE_EVENTS);
        this.componentTracker = new ComponentTracker();
        this.completionTracker = new CompletionTracker(project, module, componentTracker);

        // We load the modules in the following order:
        // 1. flow-control module (e.g flow-ref, router, fork): these components are immutable.
        // 2. maven-dependencies module (e.g module-ftp, module-csv): these components might change depending on the pom.xml
        // 3. project-custom components module (e.g from the current project): these components change when the code in the current project is modified.
        asyncLoadFlowControlModuleDescriptor((nextAction1) ->
                asyncLoadMavenDependenciesComponents((nextAction2) ->
                        asyncLoadModuleCustomComponents()));
    }

    @Override
    public Collection<ModuleDTO> listModules() {
        List<ModuleDTO> descriptors;
        synchronized (this) {
            descriptors = new ArrayList<>(mavenModules);
            if (flowControlModule != null) descriptors.add(flowControlModule);
            if (currentModule != null) descriptors.add(currentModule);
        }
        return unmodifiableCollection(descriptors);
    }

    @Override
    public synchronized ComponentDescriptor componentDescriptorFrom(String componentFullyQualifiedName) {
        return componentTracker.componentDescriptorFrom(componentFullyQualifiedName);
    }

    @Override
    public synchronized Collection<Suggestion> suggestionsOf(String inputFullyQualifiedName, String componentPropertyPath, String[] tokens) {
        return completionTracker.suggestionsOf(inputFullyQualifiedName, componentPropertyPath, tokens);
    }

    @Override
    public synchronized Collection<Suggestion> variablesOf(String inputFullyQualifiedName, String componentPropertyPath) {
        return completionTracker.variablesOf(inputFullyQualifiedName, componentPropertyPath);
    }

    @Override
    public synchronized void inputOutputOf(ContainerContext context, String outputFullyQualifiedName) {
        completionTracker.inputOutputOf(context, outputFullyQualifiedName);
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        // Remove all components before updating them
        synchronized (this) {
            mavenModules.clear();
            componentTracker.clearMaven();
            completionTracker.clearMaven();
        }

        // Notify modules changed
        onModuleChange();

        // Update modules from Maven dependencies
        asyncLoadMavenDependenciesComponents(noNextAction -> {});
    }

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        if (!aborted && errors == 0) {
            synchronized (this) {
                currentModule = null;
                componentTracker.clearCurrent();
                completionTracker.clearCurrent();
            }

            // Notify modules changed
            onModuleChange();

            // Update modules from custom project
            asyncLoadModuleCustomComponents();
        }
    }

    private void asyncLoadFlowControlModuleDescriptor(Callback<Void> nextAction) {
        PluginExecutors.run(module, message("module.component.update.system.components"),
                new LoadFlowControlModuleDescriptor(nextAction, flowControlModuleDescriptor -> {
                    ModuleDTO dto = ModuleDTOConverter.from(flowControlModuleDescriptor);
                    synchronized (this) {
                        flowControlModule = dto;
                        componentTracker.registerFlowControl(flowControlModuleDescriptor);
                        completionTracker.registerFlowControl(flowControlModuleDescriptor);
                        onModuleChange();
                    }
                }));
    }

    private void asyncLoadMavenDependenciesComponents(Callback<Void> nextAction) {
        PluginExecutors.run(module, message("module.component.update.component.for.module", module.getName()),
                new LoadMavenDependenciesModuleDescriptorsTask(module, nextAction, mavenModuleDescriptor -> {
                    ModuleDTO dto = ModuleDTOConverter.from(mavenModuleDescriptor);
                    synchronized (this) {
                        mavenModules.add(dto);
                        componentTracker.registerMaven(mavenModuleDescriptor);
                        completionTracker.registerMaven(mavenModuleDescriptor);
                        onModuleChange();
                    }
                }));
    }

    private void asyncLoadModuleCustomComponents() {
        PluginExecutors.run(module, message("module.component.update.component.for.module", module.getName()),
                new LoadModuleCustomComponentModuleDescriptor(module, moduleCustomDescriptor -> {
                    ModuleDTO dto = ModuleDTOConverter.from(moduleCustomDescriptor);
                    synchronized (this) {
                        currentModule = dto;
                        componentTracker.registerCurrent(moduleCustomDescriptor);
                        completionTracker.registerCurrent(moduleCustomDescriptor);
                        onModuleChange();
                    }
                }));
    }

    private void onModuleChange() {
        Collection<ModuleDTO> moduleComponents = listModules();
        publisher.onModuleChange(moduleComponents);
    }
}
