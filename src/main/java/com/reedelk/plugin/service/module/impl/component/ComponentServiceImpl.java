package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentScanner;
import com.reedelk.plugin.topic.ReedelkTopics;
import com.reedelk.runtime.commons.ModuleUtils;
import com.reedelk.runtime.component.Stop;
import com.reedelk.runtime.component.Unknown;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private static final String SYSTEM_COMPONENTS_MODULE_NAME = "flow-control";

    private boolean isInitialized = false;
    private final Module module;
    private final Project project;
    private final ComponentListUpdateNotifier publisher;
    private final ComponentScanner componentScanner = new ComponentScanner();

    private final ModuleComponents systemComponents;
    private final Map<String, ModuleComponents> mavenJarComponentsMap = new ConcurrentHashMap<>();
    private final List<AutoCompleteContributorDefinition> autoCompleteContributorDefinitions = new ArrayList<>();

    private ModuleComponents moduleComponents;

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
        asyncUpdateMavenDependenciesComponents();
        asyncUpdateModuleCustomComponents();
    }

    @Override
    public ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName) {

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
    public Collection<ModuleComponents> getModuleComponents() {
        List<ModuleComponents> descriptors = new ArrayList<>(mavenJarComponentsMap.values());
        descriptors.add(systemComponents);
        if (moduleComponents != null) descriptors.add(moduleComponents);
        return unmodifiableCollection(descriptors);
    }

    @Override
    public Collection<AutoCompleteContributorDefinition> getAutoCompleteContributorDefinition() {
        return unmodifiableList(autoCompleteContributorDefinitions);
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        asyncUpdateMavenDependenciesComponents();
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
    private void asyncUpdateMavenDependenciesComponents() {
        // Remove all components before updating them
        mavenJarComponentsMap.clear();
        autoCompleteContributorDefinitions.clear();

        publisher.onComponentListUpdate();

        // Update the components definitions from maven project
        MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject -> {

            // We only want the root dependencies, since user defined modules are in the root.
            mavenProject.getDependencyTree().stream()
                    .map(MavenArtifactNode::getArtifact)
                    .filter(ExcludedArtifactsFromModuleSync.predicate())
                    .filter(artifact -> ModuleUtils.isModule(artifact.getFile()))
                    .map(artifact -> artifact.getFile().getPath()).collect(toList())
                    .forEach(jarFilePath -> ModuleUtils.getModuleName(jarFilePath).ifPresent(moduleName ->
                            PluginExecutors.run(module, indicator -> scanForComponentsFromJar(jarFilePath, moduleName))));
        });
    }

    private void asyncUpdateModuleCustomComponents() {
        PluginExecutors.run(module, indicator -> {
            String[] modulePaths = ModuleRootManager.getInstance(module)
                    .orderEntries()
                    .withoutSdk()
                    .withoutLibraries()
                    .productionOnly()
                    .classes()
                    .getUrls();
            stream(modulePaths).forEach(modulePath -> {
                ScanResult scanResult = ComponentScanner.scanResultFrom(modulePath);
                List<ComponentDescriptor> components = componentScanner.from(scanResult);
                MavenUtils.getMavenProject(project, module.getName()).ifPresent(mavenProject -> {
                    String moduleName = mavenProject.getDisplayName();
                    moduleComponents = new ModuleComponents(moduleName, components);
                });
            });
            publisher.onComponentListUpdate();
        });
    }

    private void asyncUpdateSystemComponents() {
        PluginExecutors.run(module, indicator -> {
            List<ComponentDescriptor> flowControlComponents = componentScanner.from(Stop.class.getPackage());
            systemComponents.addAll(flowControlComponents);
            isInitialized = true;
            publisher.onComponentListUpdate();
        });

    }

    private void scanForComponentsFromJar(String jarFilePath, String moduleName) {
        // We only scan a module if its jar file is a module with a name.
        ScanResult scanResult = ComponentScanner.scanResultFrom(jarFilePath);
        List<ComponentDescriptor> components = componentScanner.from(scanResult);

        ModuleComponents descriptor = new ModuleComponents(moduleName, components);
        mavenJarComponentsMap.put(jarFilePath, descriptor);

        List<String> from = componentScanner.autoCompleteFrom(scanResult);
        autoCompleteContributorDefinitions.add(new AutoCompleteContributorDefinition(from));

        publisher.onComponentListUpdate();
    }

    private Optional<ComponentDescriptor> findComponentMatching(Collection<ModuleComponents> descriptors, String fullyQualifiedName) {
        for (ModuleComponents descriptor : descriptors) {
            Optional<ComponentDescriptor> moduleComponent = descriptor.getModuleComponent(fullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent;
            }
        }
        return Optional.empty();
    }
}
