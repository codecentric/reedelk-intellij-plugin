package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.ModuleDescriptor;
import com.reedelk.module.descriptor.ModuleDescriptorException;
import com.reedelk.module.descriptor.analyzer.ModuleAnalyzer;
import com.reedelk.module.descriptor.model.AutoCompleteContributorDescriptor;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.topic.ReedelkTopics;
import com.reedelk.runtime.commons.ModuleUtils;
import com.reedelk.runtime.component.Stop;
import com.reedelk.runtime.component.Unknown;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private static final Logger LOG = Logger.getInstance(ComponentServiceImpl.class);

    private static final String SYSTEM_COMPONENTS_MODULE_NAME = "flow-control";

    private boolean isInitialized;

    private final Module module;
    private final Project project;

    private final ComponentListUpdateNotifier publisher;
    private final ModuleAnalyzer componentsAnalyzer = new ModuleAnalyzer();

    // Autocomplete
    private final List<AutoCompleteContributorDescriptor> autoCompleteContributorDefinitions = new ArrayList<>();
    private final List<AutoCompleteContributorDescriptor> moduleCompleteContributorDefinitions = new ArrayList<>();

    // Components
    private ModuleComponents moduleComponents; // Current module this service is referring to.
    private final ModuleComponents systemComponents; // Default system components
    private final Map<String, ModuleComponents> mavenJarComponentsMap = new HashMap<>(); // Components coming from imported maven jars.

    public ComponentServiceImpl(Project project, Module module) {
        this.module = module;
        this.project = project;

        MessageBus messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        this.publisher = messageBus.syncPublisher(ReedelkTopics.COMPONENTS_UPDATE_EVENTS);
        this.systemComponents = new ModuleComponents(SYSTEM_COMPONENTS_MODULE_NAME, new ArrayList<>());

        asyncUpdateSystemComponents();
    }

    @Override
    public synchronized ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName) {

        // Is it part of an imported Jar from Maven ?
        Optional<ComponentDescriptor> componentMatching = findComponentMatching(mavenJarComponentsMap.values(), componentFullyQualifiedName);
        if (componentMatching.isPresent()) {
            return componentMatching.get();
        }

        // Is it a system component ?
        Optional<ComponentDescriptor> systemComponent = systemComponents.getModuleComponent(componentFullyQualifiedName);
        if (systemComponent.isPresent()) {
            return systemComponent.get();
        }

        // Is it a component in this module being developed ?
        if (moduleComponents != null) {
            Optional<ComponentDescriptor> moduleComponent = moduleComponents.getModuleComponent(componentFullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent.get();
            }

        }
        // The component is not known
        ComponentDescriptor unknownComponentDescriptor = componentDescriptorByName(Unknown.class.getName());
        return new UnknownComponentDescriptorWrapper(unknownComponentDescriptor);
    }

    @Override
    public synchronized Collection<ModuleComponents> getModuleComponents() {
        List<ModuleComponents> descriptors = new ArrayList<>(mavenJarComponentsMap.values());
        descriptors.add(systemComponents);
        if (moduleComponents != null) {
            descriptors.add(moduleComponents);
        }
        return unmodifiableCollection(descriptors);
    }

    @Override
    public synchronized Collection<AutoCompleteContributorDescriptor> getAutoCompleteContributorDescriptors() {
        List<AutoCompleteContributorDescriptor> allAutoCompletions = new ArrayList<>(autoCompleteContributorDefinitions);
        allAutoCompletions.addAll(moduleCompleteContributorDefinitions);
        return unmodifiableList(allAutoCompletions);
    }

    @Override
    public synchronized boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        // Remove all components before updating them
        mavenJarComponentsMap.clear();
        autoCompleteContributorDefinitions.clear();

        // Notify that all components have been removed
        notifyComponentListUpdate();

        // Update Maven dependencies components and do nothing when its done.
        asyncUpdateMavenDependenciesComponents(() -> {
            // Nothing
        });
    }

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        if (!aborted && errors == 0) {
            asyncUpdateModuleCustomComponents();
        }
    }

    /**
     * Updates available ESB components from maven dependencies. Components for a given
     * JAR are scanned if and only if the module name for the current JAR file is present:
     *
     * @see com.reedelk.runtime.commons.ModuleUtils#getModuleName(String)
     * If a JAR contains a module but the module name Manifest attribute does not exists,
     * then it is ignored.
     */
    private void asyncUpdateMavenDependenciesComponents(OnDone callback) {
        PluginExecutors.run(module,
                message("module.component.update.component.for.module", module.getName()),
                indicator -> {
                    // Update the components definitions from maven project
                    MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject -> {
                        // We only want the root dependencies, since user defined modules are in the root.
                        mavenProject.getDependencyTree().stream()
                                .map(MavenArtifactNode::getArtifact)
                                .filter(ExcludedArtifactsFromModuleSync.predicate())
                                .filter(artifact -> ModuleUtils.isModule(artifact.getFile()))
                                .map(artifact -> artifact.getFile().getPath()).collect(toList())
                                .forEach(jarFilePath -> ModuleUtils.getModuleName(jarFilePath)
                                        .ifPresent(esbModuleName -> {
                                            scanForComponentsOfJar(jarFilePath, esbModuleName);
                                            notifyComponentListUpdate();
                                        }));
                    });
                    callback.execute();
                });
    }

    private void asyncUpdateModuleCustomComponents() {
        PluginExecutors.run(module,
                message("module.component.update.component.for.module", module.getName()),
                indicator -> {
                    String[] modulePaths = ModuleRootManager.getInstance(module)
                            .orderEntries()
                            .withoutSdk()
                            .withoutLibraries()
                            .productionOnly()
                            .classes()
                            .getUrls();
                    stream(modulePaths).forEach(moduleTargetClassesDirectory -> {

                        ModuleDescriptor packageComponents;
                        try {
                            packageComponents = componentsAnalyzer.fromClassesFolder(moduleTargetClassesDirectory, true);
                        } catch (ModuleDescriptorException e) {
                            String message = message("module.analyze.error", module.getName(), e.getMessage());
                            LOG.error(message, e);
                            return;
                        }

                        MavenUtils.getMavenProject(project, module.getName()).ifPresent(mavenProject -> {

                            List<ComponentDescriptor> componentDescriptors = packageComponents.getComponentDescriptors();
                            String moduleName = mavenProject.getDisplayName();
                            List<AutoCompleteContributorDescriptor> moduleContributions =
                                    packageComponents.getAutocompleteContributorDescriptors();

                            synchronized (ComponentServiceImpl.this) {
                                moduleComponents = new ModuleComponents(moduleName, componentDescriptors);
                                moduleCompleteContributorDefinitions.clear();
                                moduleCompleteContributorDefinitions.addAll(moduleContributions);
                            }
                        });
                    });
                    notifyComponentListUpdate();
                });
    }

    private void asyncUpdateSystemComponents() {
        PluginExecutors.run(module,
                message("module.component.update.system.components"),
                indicator -> {
                    List<ComponentDescriptor> flowControlComponents = componentsAnalyzer.from(Stop.class);

                    synchronized (ComponentServiceImpl.class) {
                        systemComponents.addAll(flowControlComponents);
                        isInitialized = true;
                    }

                    notifyComponentListUpdate();

                    // Update maven dependencies and then current
                    // module dependencies when the task is complete.
                    asyncUpdateMavenDependenciesComponents(this::asyncUpdateModuleCustomComponents);
                });
    }

    private void scanForComponentsOfJar(String jarFilePath, String moduleName) {
        // We only scan a module if its jar file is a module with a name.
        ModuleDescriptor packageComponents;
        try {
            packageComponents = componentsAnalyzer.from(jarFilePath);
        } catch (ModuleDescriptorException e) {
            String message = message("module.analyze.jar.error", jarFilePath, moduleName, e.getMessage());
            LOG.error(message, e);
            return;
        }

        List<ComponentDescriptor> componentDescriptors = packageComponents.getComponentDescriptors();

        synchronized (ComponentServiceImpl.class) {
            // Add them to the map of components
            ModuleComponents descriptor = new ModuleComponents(moduleName, componentDescriptors);
            mavenJarComponentsMap.put(jarFilePath, descriptor);

            List<AutoCompleteContributorDescriptor> autocompleteContributorDefinitions =
                    packageComponents.getAutocompleteContributorDescriptors();
            autoCompleteContributorDefinitions.addAll(autocompleteContributorDefinitions);
        }
    }

    private static Optional<ComponentDescriptor> findComponentMatching(Collection<ModuleComponents> descriptors, String fullyQualifiedName) {
        for (ModuleComponents descriptor : descriptors) {
            Optional<ComponentDescriptor> moduleComponent = descriptor.getModuleComponent(fullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent;
            }
        }
        return Optional.empty();
    }

    private void notifyComponentListUpdate() {
        Collection<ModuleComponents> moduleComponents = getModuleComponents();
        publisher.onComponentListUpdate(moduleComponents);
    }

    interface OnDone {
        void execute();
    }
}
