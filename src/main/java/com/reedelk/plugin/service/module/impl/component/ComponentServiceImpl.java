package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.ModuleDescriptor;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.component.type.unknown.Unknown;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.topic.ReedelkTopics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Collections.unmodifiableCollection;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private final Module module;
    private final ComponentListUpdateNotifier publisher;

    private ModuleDescriptor flowControlModule; // system components module (loaded only once)
    private ModuleDescriptor currentModule; // current custom components module being developed (refreshed when compiled)
    private final List<ModuleDescriptor> mavenModules = new ArrayList<>(); // components from Maven dependencies (refreshed when Maven import finished)

    public ComponentServiceImpl(Project project, Module module) {
        this.module = module;

        MessageBus messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        this.publisher = messageBus.syncPublisher(ReedelkTopics.COMPONENTS_UPDATE_EVENTS);

        // When the service is initialized, then:
        // 1. Load Runtime Commons Module (i.e system components)
        // 2. Load Maven Dependencies Components
        // 3. Custom components (e.g from the current project).
        asyncLoadRuntimeCommonsModule(() ->
                asyncLoadMavenDependenciesComponents(() ->
                        asyncLoadModuleCustomComponents()));
    }

    @Override
    public synchronized ComponentDescriptor findComponentDescriptorBy(String componentFullyQualifiedName) {
        // Is it a system component?
        if (flowControlModule != null) {
            Optional<ComponentDescriptor> systemComponent = findComponentBy(flowControlModule.getComponents(), componentFullyQualifiedName);
            if (systemComponent.isPresent()) {
                return systemComponent.get();
            }
        }

        // Is it a component from a maven dependency?
        Optional<ComponentDescriptor> componentMatching = findComponentMatching(mavenModules, componentFullyQualifiedName);
        if (componentMatching.isPresent()) {
            return componentMatching.get();
        }

        // Is it a component in the current module being developed?
        if (currentModule != null) {
            Optional<ComponentDescriptor> moduleComponent = findComponentBy(currentModule.getComponents(), componentFullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent.get();
            }
        }

        // Then the component is not known.
        return new UnknownComponentDescriptorWrapper(Unknown.DESCRIPTOR);
    }

    @Override
    public Collection<ModuleDescriptor> getAllModuleComponents() {
        List<ModuleDescriptor> descriptors;
        synchronized (ComponentServiceImpl.class) {
            descriptors = new ArrayList<>(mavenModules);
            if (flowControlModule != null) descriptors.add(flowControlModule);
            if (currentModule != null) descriptors.add(currentModule);
        }
        return unmodifiableCollection(descriptors);
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        // Remove all components before updating them
        mavenModules.clear();

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
            asyncLoadModuleCustomComponents();
        }
    }

    /**
     * Updates available components from runtime commons module.
     */
    private void asyncLoadRuntimeCommonsModule(OnDone callback) {
        PluginExecutors.run(module, message("module.component.update.system.components"),
                new LoadRuntimeModuleDescriptor(callback, runtimeModuleDescriptor -> {
                    synchronized (ComponentServiceImpl.class) {
                        flowControlModule = runtimeModuleDescriptor;
                        notifyComponentListUpdate();
                    }
                }));
    }

    /**
     * Updates available components from maven dependencies. Components for a given
     * JAR are scanned if and only if the module name property for the current JAR file is
     * present in the JAR manifest.
     */
    private void asyncLoadMavenDependenciesComponents(OnDone callback) {
        PluginExecutors.run(module, message("module.component.update.component.for.module", module.getName()),
                new LoadMavenDependenciesModuleDescriptorsTask(module, callback, loadedModuleDescriptor -> {
                    synchronized (ComponentServiceImpl.class) {
                        mavenModules.add(loadedModuleDescriptor);
                        notifyComponentListUpdate();
                    }
                }));
    }

    private void asyncLoadModuleCustomComponents() {
        PluginExecutors.run(module, message("module.component.update.component.for.module", module.getName()),
                new LoadModuleCustomComponentModuleDescriptor(module, moduleCustomDescriptor -> {
                    synchronized (ComponentServiceImpl.this) {
                        currentModule = moduleCustomDescriptor;
                        notifyComponentListUpdate();
                    }
                }));
    }

    private static Optional<ComponentDescriptor> findComponentMatching(Collection<ModuleDescriptor> descriptors, String fullyQualifiedName) {
        for (ModuleDescriptor descriptor : descriptors) {
            Optional<ComponentDescriptor> moduleComponent = findComponentBy(descriptor.getComponents(), fullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent;
            }
        }
        return Optional.empty();
    }

    private void notifyComponentListUpdate() {
        Collection<ModuleDescriptor> moduleComponents = getAllModuleComponents();
        publisher.onComponentListUpdate(moduleComponents);
    }

    private static Optional<ComponentDescriptor> findComponentBy(List<ComponentDescriptor> components, String fullyQualifiedName) {
        return components.stream()
                .filter(componentDescriptor -> componentDescriptor.getFullyQualifiedName().equals(fullyQualifiedName))
                .findFirst();
    }
}
