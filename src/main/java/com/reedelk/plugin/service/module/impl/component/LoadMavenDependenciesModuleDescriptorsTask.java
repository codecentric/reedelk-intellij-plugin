package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.reedelk.module.descriptor.ModuleDescriptor;
import com.reedelk.module.descriptor.ModuleDescriptorException;
import com.reedelk.module.descriptor.analyzer.ModuleDescriptorAnalyzer;
import com.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import com.reedelk.plugin.executor.AsyncProgressTask;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.runtime.commons.ModuleUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArtifactNode;

import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.stream.Collectors.toList;

class LoadMavenDependenciesModuleDescriptorsTask implements AsyncProgressTask {

    private static final Logger LOG = Logger.getInstance(LoadMavenDependenciesModuleDescriptorsTask.class);

    private final Module module;
    private final OnDone callback;
    private final OnModuleDescriptorLoaded moduleDescriptorLoaded;
    private final ModuleDescriptorAnalyzer moduleAnalyzer = new ModuleDescriptorAnalyzer();

    LoadMavenDependenciesModuleDescriptorsTask(Module module, OnDone callback, OnModuleDescriptorLoaded moduleDescriptorLoaded) {
        this.module = module;
        this.callback = callback;
        this.moduleDescriptorLoaded = moduleDescriptorLoaded;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        // Update the components definitions from maven project
        MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject -> {
            // We only want the root dependencies, since user defined modules are in the root.
            mavenProject.getDependencyTree().stream()
                    .map(MavenArtifactNode::getArtifact)
                    .filter(ExcludedArtifactsFromModuleSync.predicate())
                    .filter(artifact -> ModuleUtils.isModule(artifact.getFile()))
                    .map(artifact -> artifact.getFile().getPath()).collect(toList())
                    .forEach(jarFilePath ->
                            ModuleUtils.getModuleName(jarFilePath) // We only scan a module if its jar file is a module with a name.
                                    .flatMap(moduleName -> readModuleDescriptorFromJarFile(jarFilePath, moduleName))
                                    .ifPresent(moduleDescriptorLoaded::onItem));
        });

        callback.execute();
    }

    private Optional<ModuleDescriptor> readModuleDescriptorFromJarFile(String jarFilePath, String moduleName) {
        try {
            return Optional.of(moduleAnalyzer.from(jarFilePath, moduleName));
        } catch (ModuleDescriptorException e) {
            String message = message("module.analyze.jar.error", jarFilePath, moduleName, e.getMessage());
            LOG.error(message, e);
            return Optional.empty();
        }
    }
}
