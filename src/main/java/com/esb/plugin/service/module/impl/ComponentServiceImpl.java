package com.esb.plugin.service.module.impl;

import com.esb.plugin.commons.ESBModuleInfo;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.unknown.UnknownComponentDescriptorWrapper;
import com.esb.plugin.service.module.ComponentService;
import com.esb.plugin.service.module.impl.esbcomponent.ComponentListUpdateNotifier;
import com.esb.plugin.service.module.impl.esbcomponent.ComponentScanner;
import com.esb.plugin.service.module.impl.esbmodule.ModuleAnalyzer;
import com.esb.plugin.service.module.impl.esbmodule.ModuleDescriptor;
import com.esb.system.component.Stop;
import com.esb.system.component.Unknown;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ComponentServiceImpl implements ComponentService, MavenImportListener {

    private static final Logger LOG = Logger.getInstance(ComponentServiceImpl.class);

    private static final String SYSTEM_JAR_PATH = "flow-control";

    private final Module module;

    private final ComponentListUpdateNotifier publisher;

    private Map<String, ModuleDescriptor> jarFilePathModuleDescriptorMap = new HashMap<>();


    /**
     * The constructor loads the system components. Synchronously.
     * All the other components are loaded asynchronously.
     *
     * @param module the module this service is referring to.
     */
    public ComponentServiceImpl(Project project, Module module, MessageBus messageBus) {
        this.module = module;

        List<ComponentDescriptor> coreComponents = ComponentScanner.getComponentsFromPackage(Stop.class.getPackage().getName());
        jarFilePathModuleDescriptorMap.put(SYSTEM_JAR_PATH, new ModuleDescriptor("Flow Control", coreComponents));

        publisher = messageBus.syncPublisher(ComponentListUpdateNotifier.COMPONENT_LIST_UPDATE_TOPIC);
        asyncScanClasspathComponents();

        project.getMessageBus().connect().subscribe(MavenImportListener.TOPIC, this);
    }

    @Override
    public ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName) {
        Collection<ModuleDescriptor> values = jarFilePathModuleDescriptorMap.values();
        for (ModuleDescriptor descriptor : values) {
            Optional<ComponentDescriptor> moduleComponent = descriptor.getModuleComponent(componentFullyQualifiedName);
            if (moduleComponent.isPresent()) {
                return moduleComponent.get();
            }
        }
        // The component for the given fully qualified name is not known.
        return new UnknownComponentDescriptorWrapper(componentDescriptorByName(Unknown.class.getName()));
    }

    @Override
    public Collection<ModuleDescriptor> getModulesDescriptors() {
        return jarFilePathModuleDescriptorMap.values();
    }

    private void asyncScanClasspathComponents() {
        CompletableFuture.supplyAsync(() -> {

            // TODO: This method is craap!
            List<String> classPathEntries = ModuleRootManager.getInstance(module)
                    .orderEntries()
                    .withoutSdk()
                    .withoutDepModules()
                    .librariesOnly()
                    .classes()
                    .getPathsList()
                    .getPathList();

            List<String> jarFilePaths = classPathEntries.stream()
                    .filter(ESBModuleInfo::IsESBModule)
                    .collect(Collectors.toList());


            Set<String> oldJarFilePaths = jarFilePathModuleDescriptorMap.keySet();
            Set<String> toRemove = new HashSet<>();
            oldJarFilePaths.forEach(s -> {
                if (!jarFilePaths.contains(s) && !s.equals(SYSTEM_JAR_PATH)) toRemove.add(s);
            });

            toRemove.forEach(s -> jarFilePathModuleDescriptorMap.remove(s));

            ModuleAnalyzer moduleAnalyzer = new ModuleAnalyzer();
            jarFilePaths.forEach(jarFilePath -> {
                ModuleDescriptor descriptor = moduleAnalyzer.analyze(jarFilePath);
                if (jarFilePathModuleDescriptorMap.containsKey(jarFilePath)) {
                    jarFilePathModuleDescriptorMap.replace(jarFilePath, descriptor);
                } else {
                    jarFilePathModuleDescriptorMap.put(jarFilePath, descriptor);
                }
                publisher.onComponentListUpdate();
            });


            /**
             String[] currentProjectClassPathEntries = ModuleRootManager.getInstance(module)
             .orderEntries()
             .withoutSdk()
             .withoutLibraries()
             .sources()
             .getUrls();
             Arrays.stream(currentProjectClassPathEntries).forEach(new Consumer<String>() {
            @Override public void accept(String s) {
            List<ComponentDescriptor> componentDescriptor = ComponentScanner.scan(s);
            System.out.println("yess");
            }
            });
             */

            return null;
        });

    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {
        asyncScanClasspathComponents();
    }
}
