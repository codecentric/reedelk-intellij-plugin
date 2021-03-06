package de.codecentric.reedelk.plugin.service.module.impl.component;

import de.codecentric.reedelk.plugin.commons.Topics;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.module.*;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.executor.PluginExecutors;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Collections.unmodifiableCollection;

public class PlatformModuleServiceImpl implements PlatformModuleService, MavenImportListener, CompilationStatusListener {

    private final Module module;
    private final OnModuleEvent publisher;

    // MODULES
    private ModuleDTO flowControlModule; // system components module (loaded only once)
    private ModuleDTO currentModule; // current custom components module being developed (refreshed when compiled)
    private final List<ModuleDTO> mavenModules = new ArrayList<>(); // components from Maven dependencies (refreshed when Maven import finished)

    private PlatformComponentService componentTracker;
    private PlatformCompletionService platformCompletionService;

    public PlatformModuleServiceImpl(Project project, Module module) {
        MessageBus messageBus = project.getMessageBus();
        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        this.module = module;
        this.publisher = messageBus.syncPublisher(Topics.COMPONENTS_UPDATE_EVENTS);
        this.componentTracker = new PlatformComponentService();
        this.platformCompletionService = new PlatformCompletionService(module, this);

        // We load the modules in the following order:
        // 1. flow-control module (e.g flow-ref, router, fork): these components are immutable.
        // 2. maven-dependencies module (e.g module-ftp, module-csv): these components might change depending on the pom.xml
        // 3. project-custom components module (e.g from the current project): these components change when the code in the current project is modified.
        asyncLoadFlowControlModuleDescriptor(nextAction1 ->
                asyncLoadMavenDependenciesComponents(nextAction2 ->
                        asyncLoadModuleCustomComponents()));
    }

    @Override
    public Collection<ModuleDTO> listModules() {
        synchronized (this) {
            List<ModuleDTO> descriptors = new ArrayList<>(mavenModules);
            if (flowControlModule != null) descriptors.add(flowControlModule);
            if (currentModule != null) descriptors.add(currentModule);
            return unmodifiableCollection(descriptors);
        }
    }

    @Override
    public synchronized ComponentDescriptor componentDescriptorOf(@NotNull String componentFullyQualifiedName) {
        return componentTracker.componentDescriptorOf(componentFullyQualifiedName);
    }

    @Override
    public synchronized Collection<Suggestion> suggestionsOf(@NotNull ComponentContext context, @NotNull String componentPropertyPath, String[] tokens) {
        return platformCompletionService.suggestionsOf(context, componentPropertyPath, tokens);
    }

    @Override
    public synchronized Collection<Suggestion> variablesOf(@NotNull ComponentContext context, @NotNull String componentPropertyPath) {
        return platformCompletionService.variablesOf(context, componentPropertyPath);
    }

    @Override
    public synchronized void componentMetadataOf(@NotNull ComponentContext context) {
        platformCompletionService.componentMetadataOf(context);
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        // Remove all components before updating them
        synchronized (this) {
            mavenModules.clear();
            componentTracker.clearMaven();
            platformCompletionService.clearMaven();
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
                platformCompletionService.clearCurrent();
            }

            // Notify modules changed
            onModuleChange();

            // Update modules from custom project
            asyncLoadModuleCustomComponents();
        }
    }

    private void asyncLoadFlowControlModuleDescriptor(Callback<Void> nextAction) {
        PluginExecutors.run(module, ReedelkBundle.message("module.component.update.system.components"),
                new LoadFlowControlModuleDescriptor(nextAction, flowControlModuleDescriptor -> {
                    ModuleDTO dto = ModuleDTOConverter.from(flowControlModuleDescriptor);
                    synchronized (this) {
                        flowControlModule = dto;
                        componentTracker.registerFlowControl(flowControlModuleDescriptor);
                        platformCompletionService.registerFlowControl(flowControlModuleDescriptor);
                    }
                    onModuleChange();
                }));
    }

    private void asyncLoadMavenDependenciesComponents(Callback<Void> nextAction) {
        PluginExecutors.run(module, ReedelkBundle.message("module.component.update.component.for.module", module.getName()),
                new LoadMavenDependenciesModuleDescriptorsTask(module, nextAction, mavenModuleDescriptor -> {
                    ModuleDTO dto = ModuleDTOConverter.from(mavenModuleDescriptor);
                    synchronized (this) {
                        mavenModules.add(dto);
                        componentTracker.registerMaven(mavenModuleDescriptor);
                        platformCompletionService.registerMaven(mavenModuleDescriptor);
                    }
                    onModuleChange();
                }));
    }

    private void asyncLoadModuleCustomComponents() {
        PluginExecutors.run(module, ReedelkBundle.message("module.component.update.component.for.module", module.getName()),
                new LoadModuleCustomComponentModuleDescriptor(module, moduleCustomDescriptor -> {
                    ModuleDTO dto = ModuleDTOConverter.from(moduleCustomDescriptor);
                    synchronized (this) {
                        currentModule = dto;
                        componentTracker.registerCurrent(moduleCustomDescriptor);
                        platformCompletionService.registerCurrent(moduleCustomDescriptor);
                    }
                    onModuleChange();
                }));
    }

    private void onModuleChange() {
        Collection<ModuleDTO> moduleComponents = listModules();
        publisher.onModuleUpdated(moduleComponents);
    }
}
