package com.esb.plugin.service.module.impl;

import com.esb.plugin.commons.ESBModuleInfo;
import com.esb.plugin.commons.MavenUtils;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentsDescriptor;
import com.esb.plugin.component.scanner.ComponentListUpdateNotifier;
import com.esb.plugin.component.scanner.ComponentScanner;
import com.esb.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.esb.plugin.service.module.ComponentService;
import com.esb.system.component.Stop;
import com.esb.system.component.Unknown;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private final Module module;
    private final Project project;
    private final ComponentListUpdateNotifier publisher;
    private final ComponentScanner componentScanner = new ComponentScanner();

    private final ComponentsDescriptor systemComponents;
    private final Map<String, ComponentsDescriptor> mavenJarComponentsMap = new HashMap<>();

    private ComponentsDescriptor moduleComponents;


    /**
     * The constructor loads the system components. Synchronously.
     * All the other components are loaded asynchronously.
     *
     * @param module the module this service is referring to.
     */
    public ComponentServiceImpl(Project project, Module module, MessageBus messageBus) {
        this.module = module;
        this.project = project;
        this.publisher = messageBus.syncPublisher(ComponentListUpdateNotifier.COMPONENT_LIST_UPDATE_TOPIC);

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        systemComponents = scanSystemComponents();
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
        Optional<ComponentDescriptor> moduleComponent = moduleComponents.getModuleComponent(componentFullyQualifiedName);
        if (moduleComponent.isPresent()) {
            return moduleComponent.get();
        }

        // The component is not known
        return new UnknownComponentDescriptorWrapper(componentDescriptorByName(Unknown.class.getName()));
    }

    @Override
    public Collection<ComponentsDescriptor> getModulesDescriptors() {
        List<ComponentsDescriptor> descriptors = new ArrayList<>(mavenJarComponentsMap.values());
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

            ModuleRootManager.getInstance(module)
                    .orderEntries()
                    .withoutSdk()
                    .withoutDepModules()
                    .librariesOnly()
                    .classes()
                    .getPathsList()
                    .getPathList()
                    .stream()
                    .filter(ESBModuleInfo::IsESBModule)
                    .collect(Collectors.toList())
                    .forEach(jarFilePath -> {
                        List<ComponentDescriptor> components = componentScanner.from(jarFilePath);
                        String moduleName = ESBModuleInfo.GetESBModuleName(jarFilePath);
                        ComponentsDescriptor descriptor = new ComponentsDescriptor(moduleName, components);
                        mavenJarComponentsMap.put(jarFilePath, descriptor);
                        publisher.onComponentListUpdate();
                    });
        });
    }

    private void asyncUpdateModuleComponents() {
        CompletableFuture.runAsync(() ->
                Arrays.stream(ModuleRootManager.getInstance(module)
                        .orderEntries()
                        .withoutSdk()
                        .withoutLibraries()
                        .productionOnly()
                        .classes()
                        .getUrls())
                        .forEach(modulePath -> {
                            List<ComponentDescriptor> components = componentScanner.from(modulePath);
                            String moduleName = MavenUtils.getMavenProject(project, module.getName()).get().getDisplayName();
                            moduleComponents = new ComponentsDescriptor(moduleName, components);
                            publisher.onComponentListUpdate();
                        }));
    }


    private ComponentsDescriptor scanSystemComponents() {
        String moduleName = "Flow Control";
        List<ComponentDescriptor> flowControlComponents = componentScanner.from(Stop.class.getPackage());
        return new ComponentsDescriptor(moduleName, flowControlComponents);
    }

    private Optional<ComponentDescriptor> findComponentMatching(Collection<ComponentsDescriptor> descriptors, String fullyQualifiedName) {
        for (ComponentsDescriptor descriptor : descriptors) {
            Optional<ComponentDescriptor> moduleComponent = descriptor.getModuleComponent(fullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent;
            }
        }
        return Optional.empty();
    }
}
