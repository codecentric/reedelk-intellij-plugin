package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.component.type.unknown.Unknown;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.ComponentService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private final Module module;
    private final ComponentListUpdateNotifier publisher;

    // MODULES
    private ModuleDTO flowControlModule; // system components module (loaded only once)
    private ModuleDTO currentModule; // current custom components module being developed (refreshed when compiled)
    private final List<ModuleDTO> mavenModules = new ArrayList<>(); // components from Maven dependencies (refreshed when Maven import finished)

    // COMPONENTS
    private final Map<String, ComponentDescriptor> flowControlModuleComponents = new HashMap<>(); // A map containing the component fully qualified name and the descriptor from flow control module
    private final Map<String, ComponentDescriptor> currentModuleComponents = new HashMap<>(); // A map containing the component fully qualified name and the descriptor from current module being developed.
    private final Map<String, ComponentDescriptor> mavenModulesComponents = new HashMap<>(); // A map containing the component fully qualified name and the descriptor from maven.

    public ComponentServiceImpl(Project project, Module module) {
        this.module = module;

        MessageBus messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        this.publisher = messageBus.syncPublisher(Topics.COMPONENTS_UPDATE_EVENTS);

        // When the service is initialized, then:
        // 1. Load Runtime Commons Module (i.e system components)
        // 2. Load Maven Dependencies Components
        // 3. Custom components (e.g from the current project).
        asyncLoadFlowControlModuleDescriptor(() ->
                asyncLoadMavenDependenciesComponents(() ->
                        asyncLoadModuleCustomComponents()));
    }

    @Override
    public synchronized ComponentDescriptor findComponentDescriptorBy(String componentFullyQualifiedName) {
        // Is it a component from a maven dependency?
        ComponentDescriptor descriptor = mavenModulesComponents.getOrDefault(componentFullyQualifiedName, null);
        if (descriptor != null) return descriptor;

        // Is it a system component?
        descriptor = flowControlModuleComponents.getOrDefault(componentFullyQualifiedName, null);
        if (descriptor != null) return descriptor;

        // Is it a component in the current module being developed?
        descriptor = currentModuleComponents.getOrDefault(componentFullyQualifiedName, null);
        if (descriptor != null) return descriptor;

        // The component is not known.
        return new UnknownComponentDescriptorWrapper(Unknown.DESCRIPTOR);
    }

    @Override
    public Collection<ModuleDTO> getModules() {
        List<ModuleDTO> descriptors;
        synchronized (this) {
            descriptors = new ArrayList<>(mavenModules);
            if (flowControlModule != null) descriptors.add(flowControlModule);
            if (currentModule != null) descriptors.add(currentModule);
        }
        return unmodifiableCollection(descriptors);
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        // Remove all components before updating them
        synchronized (this) {
            mavenModules.clear();
            mavenModulesComponents.clear();
        }

        // Notify that all components have been removed
        notifyComponentListUpdate();

        // Update Maven dependencies components and do nothing when its done.
        asyncLoadMavenDependenciesComponents(() -> {
            // Nothing
        });
    }

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        if (!aborted && errors == 0) {
            synchronized (this) {
                currentModule = null;
                currentModuleComponents.clear();
            }

            // Notify that all components have been removed
            notifyComponentListUpdate();

            // Update custom components
            asyncLoadModuleCustomComponents();
        }
    }

    /**
     * Updates available components from runtime commons module.
     */
    private void asyncLoadFlowControlModuleDescriptor(OnDone nextAction) {
        PluginExecutors.run(module, message("module.component.update.system.components"),
                new LoadFlowControlModuleDescriptor(nextAction, flowControlModuleDescriptor -> {
                    ModuleDTO dto = asModuleDTO(flowControlModuleDescriptor);
                    synchronized (this) {
                        flowControlModule = dto;
                        registerComponents(flowControlModuleDescriptor, flowControlModuleComponents);
                        notifyComponentListUpdate();
                    }
                }));
    }

    private void asyncLoadMavenDependenciesComponents(OnDone nextAction) {
        PluginExecutors.run(module, message("module.component.update.component.for.module", module.getName()),
                // We need to scan the maven dependencies, extract only the ones
                // with module descriptor in it. We create
                new LoadMavenDependenciesModuleDescriptorsTask(module, nextAction, mavenModuleDescriptor -> {
                    ModuleDTO dto = asModuleDTO(mavenModuleDescriptor);
                    synchronized (this) {
                        mavenModules.add(dto);
                        registerComponents(mavenModuleDescriptor, mavenModulesComponents);
                        notifyComponentListUpdate();
                    }
                }));
    }

    private void asyncLoadModuleCustomComponents() {
        PluginExecutors.run(module, message("module.component.update.component.for.module", module.getName()),
                new LoadModuleCustomComponentModuleDescriptor(module, moduleCustomDescriptor -> {
                    ModuleDTO dto = asModuleDTO(moduleCustomDescriptor);
                    // Register global types
                    // Register components
                    synchronized (this) {
                        currentModule = dto;
                        registerComponents(moduleCustomDescriptor, currentModuleComponents);
                        notifyComponentListUpdate();
                    }
                }));
    }

    private void registerComponents(ModuleDescriptor moduleDescriptor, Map<String, ComponentDescriptor> targetCollection) {
        moduleDescriptor.getComponents()
                .forEach(componentDescriptor ->
                        targetCollection.put(componentDescriptor.getFullyQualifiedName(), componentDescriptor));
    }

    private void notifyComponentListUpdate() {
        Collection<ModuleDTO> moduleComponents = getModules();
        publisher.onComponentListUpdate(moduleComponents);
    }

    @NotNull
    private ModuleDTO asModuleDTO(ModuleDescriptor moduleDescriptor) {
        List<ModuleComponentDTO> moduleComponentDTOs = moduleDescriptor.getComponents()
                .stream()
                .map(componentDescriptor -> new ModuleComponentDTO(
                        componentDescriptor.getFullyQualifiedName(),
                        componentDescriptor.getDisplayName(),
                        componentDescriptor.getImage(),
                        componentDescriptor.getIcon(),
                        componentDescriptor.isHidden()))
                .collect(toList());
        return new ModuleDTO(moduleDescriptor.getName(), moduleComponentDTOs);
    }
}
