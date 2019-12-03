package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.MavenUtils;
import com.reedelk.plugin.commons.ModuleInfo;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentsPackage;
import com.reedelk.plugin.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.component.scanner.ComponentScanner;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.runtime.component.Stop;
import com.reedelk.runtime.component.Unknown;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private static final String SYSTEM_COMPONENTS_MODULE_NAME = "flow-control";

    private final Module module;
    private final Project project;
    private final ComponentListUpdateNotifier publisher;
    private final ComponentScanner componentScanner = new ComponentScanner();

    private final ComponentsPackage systemComponents;
    private final Map<String, ComponentsPackage> mavenJarComponentsMap = new HashMap<>();

    private ComponentsPackage moduleComponents;


    /**
     * The constructor loads the system components. Synchronously.
     * All the other components are loaded asynchronously.
     *
     * @param module the module this service is referring to.
     */
    public ComponentServiceImpl(Project project, Module module) {
        this.module = module;
        this.project = project;

        MessageBus messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        this.publisher = messageBus.syncPublisher(ComponentListUpdateNotifier.COMPONENT_LIST_UPDATE_TOPIC);
        this.systemComponents = scanSystemComponents();

        asyncUpdateModuleComponents();
        asyncScanClasspathComponents();
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
        return new UnknownComponentDescriptorWrapper(componentDescriptorByName(Unknown.class.getName()));
    }

    @Override
    public Collection<ComponentsPackage> getModulesDescriptors() {
        List<ComponentsPackage> descriptors = new ArrayList<>(mavenJarComponentsMap.values());
        descriptors.add(systemComponents);
        if (moduleComponents != null) descriptors.add(moduleComponents);
        return Collections.unmodifiableCollection(descriptors);
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        asyncScanClasspathComponents();
    }

    @Override
    public void compilationFinished(boolean aborted, int errors, int warnings, @NotNull CompileContext compileContext) {
        if (!aborted && errors == 0) {
            asyncUpdateModuleComponents();
        }
    }

    private void asyncScanClasspathComponents() {
        CompletableFuture.runAsync(() -> {
            // Remove all jars before reimporting them
            mavenJarComponentsMap.clear();


            MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject -> {

                mavenProject.getDependencies().stream()
                        .filter(artifact -> ModuleInfo.isModule(artifact.getFile()))
                        .map(artifact -> artifact.getFile().getPath()).collect(Collectors.toList())
                        .forEach(jarFilePath -> {
                            List<ComponentDescriptor> components = componentScanner.from(jarFilePath);
                            String moduleName = ModuleInfo.getModuleName(jarFilePath);
                            ComponentsPackage descriptor = new ComponentsPackage(moduleName, components);
                            mavenJarComponentsMap.put(jarFilePath, descriptor);
                        });

                publisher.onComponentListUpdate(module);
            });

        });
    }

    private void asyncUpdateModuleComponents() {
        CompletableFuture.runAsync(() -> {
            String[] modulePaths = ModuleRootManager.getInstance(module)
                    .orderEntries()
                    .withoutSdk()
                    .withoutLibraries()
                    .productionOnly()
                    .classes()
                    .getUrls();

            stream(modulePaths).forEach(modulePath -> {
                List<ComponentDescriptor> components = componentScanner.from(modulePath);
                String moduleName = MavenUtils.getMavenProject(project, module.getName()).get().getDisplayName();
                moduleComponents = new ComponentsPackage(moduleName, components);
            });

            publisher.onComponentListUpdate(module);
        });
    }


    private ComponentsPackage scanSystemComponents() {
        List<ComponentDescriptor> flowControlComponents = componentScanner.from(Stop.class.getPackage());
        return new ComponentsPackage(SYSTEM_COMPONENTS_MODULE_NAME, flowControlComponents);
    }

    private Optional<ComponentDescriptor> findComponentMatching(Collection<ComponentsPackage> descriptors, String fullyQualifiedName) {
        for (ComponentsPackage descriptor : descriptors) {
            Optional<ComponentDescriptor> moduleComponent = descriptor.getModuleComponent(fullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent;
            }
        }
        return Optional.empty();
    }
}
