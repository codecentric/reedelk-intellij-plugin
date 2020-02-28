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
import com.reedelk.module.descriptor.analyzer.ModuleDescriptorAnalyzer;
import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import com.reedelk.plugin.component.type.unknown.Unknown;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.topic.ReedelkTopics;
import com.reedelk.runtime.commons.ModuleUtils;
import com.reedelk.runtime.component.Stop;
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

    private final Module module;

    private final ComponentListUpdateNotifier publisher;
    private final ModuleDescriptorAnalyzer moduleAnalyzer = new ModuleDescriptorAnalyzer();

    // Autocomplete
    private final List<AutocompleteItemDescriptor> autoCompleteContributorDefinitions = new ArrayList<>();
    private final List<AutocompleteItemDescriptor> moduleCompleteContributorDefinitions = new ArrayList<>();

    // Flow Control Components (e.g system components)
    // - Once loaded once, they are fixed and they cannot be updated because they are fixed in the api version -
    private ModuleDescriptor flowControlModule;
    // Current Module Descriptor - Can be refreshed
    private ModuleDescriptor currentModule;
    // Components coming from maven dependencies.
    private final Map<String, ModuleDescriptor> mavenModules = new HashMap<>();


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
        Optional<ComponentDescriptor> componentMatching = findComponentMatching(mavenModules.values(), componentFullyQualifiedName);
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
    public synchronized Collection<ModuleDescriptor> getAllModuleComponents() {
        List<ModuleDescriptor> descriptors = new ArrayList<>(mavenModules.values());
        if (flowControlModule != null) descriptors.add(flowControlModule);
        if (currentModule != null) descriptors.add(currentModule);
        return unmodifiableCollection(descriptors);
    }

    @Override
    public synchronized Collection<AutocompleteItemDescriptor> getAutoCompleteItemDescriptors() {
        List<AutocompleteItemDescriptor> allAutoCompletions = new ArrayList<>(autoCompleteContributorDefinitions);
        allAutoCompletions.addAll(moduleCompleteContributorDefinitions);
        return unmodifiableList(allAutoCompletions);
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        // Remove all components before updating them
        mavenModules.clear();
        autoCompleteContributorDefinitions.clear();

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

    private void asyncLoadRuntimeCommonsModule(OnDone callback) {
        PluginExecutors.run(module, message("module.component.update.system.components"), indicator -> {
            ModuleDescriptor descriptor = moduleAnalyzer.from(Stop.class);
            synchronized (ComponentServiceImpl.class) {
                flowControlModule = descriptor;
            }
            notifyComponentListUpdate();
            callback.execute();
        });
    }

    /**
     * Updates available components from maven dependencies. Components for a given
     * JAR are scanned if and only if the module name property for the current JAR file is
     * present in the JAR manifest.
     */
    private void asyncLoadMavenDependenciesComponents(OnDone callback) {
        // TODO: Extract the following async progress task into its own class.
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
                                .forEach(jarFilePath -> ModuleUtils.getModuleName(jarFilePath).ifPresent(moduleName -> {
                                            loadComponentsFromJar(jarFilePath, moduleName);
                                            notifyComponentListUpdate();
                                        }));
                    });
                    callback.execute();
                });
    }

    private void asyncLoadModuleCustomComponents() {
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

                        // TODO: Fixme.
                        /**
                        MavenUtils.getMavenProject(project, module.getName()).ifPresent(mavenProject -> {

                            // TODO: Is it really maven project.getdisplayname?
                            ModuleDescriptor packageComponents;
                            try {
                                packageComponents = componentsAnalyzer.fromClassesFolder(moduleTargetClassesDirectory, mavenProject.getDisplayName(), true);
                            } catch (ModuleDescriptorException e) {
                                String message = message("module.analyze.error", module.getName(), e.getMessage());
                                LOG.error(message, e);
                                return;
                            }

                            List<ComponentDescriptor> componentDescriptors = packageComponents.getComponents();
                            String moduleName = mavenProject.getDisplayName();
                            List<AutocompleteItemDescriptor> moduleContributions = packageComponents.getAutocompleteItems();
                            synchronized (ComponentServiceImpl.this) {
                                moduleComponents = new ModuleDescriptor();
                                moduleComponents
                                moduleCompleteContributorDefinitions.clear();
                                moduleCompleteContributorDefinitions.addAll(moduleContributions);
                            }
                        });*/
                    });
                    notifyComponentListUpdate();
                });
    }

    private void loadComponentsFromJar(String jarFilePath, String moduleName) {
        // We only scan a module if its jar file is a module with a name.
        ModuleDescriptor moduleDescriptor;
        try {
            moduleDescriptor = moduleAnalyzer.from(jarFilePath, moduleName);
        } catch (ModuleDescriptorException e) {
            String message = message("module.analyze.jar.error", jarFilePath, moduleName, e.getMessage());
            LOG.error(message, e);
            return;
        }

        synchronized (ComponentServiceImpl.class) {
            // Add them to the map of components
            mavenModules.put(jarFilePath, moduleDescriptor);

            List<AutocompleteItemDescriptor> autocompleteItems = moduleDescriptor.getAutocompleteItems();
            autoCompleteContributorDefinitions.addAll(autocompleteItems);
        }
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

    interface OnDone {
        void execute();
    }

    private static Optional<ComponentDescriptor> findComponentBy(List<ComponentDescriptor> components, String fullyQualifiedName) {
        return components.stream()
                .filter(componentDescriptor -> componentDescriptor.getFullyQualifiedName().equals(fullyQualifiedName))
                .findFirst();
    }
}
