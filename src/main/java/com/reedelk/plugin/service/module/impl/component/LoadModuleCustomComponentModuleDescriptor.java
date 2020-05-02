package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.roots.ModuleRootManager;
import com.reedelk.module.descriptor.ModuleDescriptorException;
import com.reedelk.module.descriptor.analyzer.ModuleDescriptorAnalyzer;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.plugin.executor.AsyncProgressTask;
import com.reedelk.plugin.maven.MavenUtils;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Arrays.stream;

class LoadModuleCustomComponentModuleDescriptor implements AsyncProgressTask {

    private static final Logger LOG = Logger.getInstance(LoadModuleCustomComponentModuleDescriptor.class);

    private final Module module;
    private final OnModuleDescriptorLoaded onModuleDescriptorLoaded;

    LoadModuleCustomComponentModuleDescriptor(Module module, OnModuleDescriptorLoaded onModuleDescriptorLoaded) {
        this.module = module;
        this.onModuleDescriptorLoaded = onModuleDescriptorLoaded;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        String[] modulePaths = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutLibraries()
                .productionOnly()
                .classes()
                .getUrls();
        stream(modulePaths).forEach(moduleTargetClassesDirectory ->
                MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject -> {
                    ModuleDescriptorAnalyzer moduleAnalyzer = new ModuleDescriptorAnalyzer();
                    try {
                        ModuleDescriptor packageComponents = moduleAnalyzer.fromDirectory(moduleTargetClassesDirectory, mavenProject.getDisplayName(), true);
                        onModuleDescriptorLoaded.onItem(packageComponents);
                    } catch (ModuleDescriptorException exception) {
                        String message = message("module.analyze.error", module.getName(), exception.getMessage());
                        LOG.error(message, exception);
                    }
                }));
    }
}
