package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.ModuleInfo;
import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentScanner;
import com.reedelk.plugin.topic.ReedelkTopics;
import com.reedelk.runtime.component.Stop;
import com.reedelk.runtime.component.Unknown;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class ComponentServiceImpl implements ComponentService, MavenImportListener, CompilationStatusListener {

    private static final String SYSTEM_COMPONENTS_MODULE_NAME = "flow-control";

    private final Module module;
    private final Project project;
    private final ComponentListUpdateNotifier publisher;
    private final ComponentScanner componentScanner = new ComponentScanner();

    private final ModuleComponents systemComponents;
    private final Map<String, ModuleComponents> mavenJarComponentsMap = new HashMap<>();
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
        return new UnknownComponentDescriptorWrapper(componentDescriptorByName(Unknown.class.getName()));
    }

    @Override
    public Collection<ModuleComponents> getModuleComponents() {
        List<ModuleComponents> descriptors = new ArrayList<>(mavenJarComponentsMap.values());
        descriptors.add(systemComponents);
        if (moduleComponents != null) descriptors.add(moduleComponents);
        return Collections.unmodifiableCollection(descriptors);
    }

    @Override
    public Collection<AutoCompleteContributorDefinition> getAutoCompleteContributorDefinition() {
        return Collections.unmodifiableList(autoCompleteContributorDefinitions) ;
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

    private void asyncUpdateMavenDependenciesComponents() {
        PluginExecutor.getInstance().submit(() -> {
            // Remove all components before updating them
            mavenJarComponentsMap.clear();
            autoCompleteContributorDefinitions.clear();

            publisher.onComponentListUpdate();

            // Update the components definitions from maven project
            MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject -> {
                mavenProject.getDependencies().stream()
                        .filter(artifact -> ModuleInfo.isModule(artifact.getFile()))
                        .map(artifact -> artifact.getFile().getPath()).collect(toList())
                        .forEach(jarFilePath -> {
                            ScanResult scanResult = ComponentScanner.scanResultFrom(jarFilePath);
                            List<ComponentDescriptor> components = componentScanner.from(scanResult);
                            String moduleName = ModuleInfo.getModuleName(jarFilePath);
                            ModuleComponents descriptor = new ModuleComponents(moduleName, components);
                            mavenJarComponentsMap.put(jarFilePath, descriptor);

                            List<String> from = componentScanner.autoCompleteFrom(scanResult);
                            autoCompleteContributorDefinitions.add(
                                    new AutoCompleteContributorDefinition(from));

                            publisher.onComponentListUpdate();
                        });
            });
        });
    }

    private void asyncUpdateModuleCustomComponents() {
        PluginExecutor.getInstance().submit(() -> {
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
        PluginExecutor.getInstance().submit(() -> {
            List<ComponentDescriptor> flowControlComponents = componentScanner.from(Stop.class.getPackage());
            this.systemComponents.addAll(flowControlComponents);
        });
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
