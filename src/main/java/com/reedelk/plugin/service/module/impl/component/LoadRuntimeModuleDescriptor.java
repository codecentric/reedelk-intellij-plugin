package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.progress.ProgressIndicator;
import com.reedelk.module.descriptor.ModuleDescriptor;
import com.reedelk.module.descriptor.analyzer.ModuleDescriptorAnalyzer;
import com.reedelk.plugin.executor.AsyncProgressTask;
import com.reedelk.runtime.component.Stop;
import org.jetbrains.annotations.NotNull;

class LoadRuntimeModuleDescriptor implements AsyncProgressTask {

    private final OnDone onDone;
    private final OnModuleDescriptorLoaded moduleDescriptorLoaded;
    private final ModuleDescriptorAnalyzer moduleAnalyzer = new ModuleDescriptorAnalyzer();

    LoadRuntimeModuleDescriptor(OnDone onDone, OnModuleDescriptorLoaded moduleDescriptorLoaded) {
        this.onDone = onDone;
        this.moduleDescriptorLoaded = moduleDescriptorLoaded;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        ModuleDescriptor descriptor = moduleAnalyzer.from(Stop.class);
        moduleDescriptorLoaded.onItem(descriptor);
        onDone.execute();
    }
}