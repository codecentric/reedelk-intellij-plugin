package de.codecentric.reedelk.plugin.service.module.impl.component.module;

import com.intellij.openapi.progress.ProgressIndicator;
import de.codecentric.reedelk.module.descriptor.analyzer.ModuleDescriptorAnalyzer;
import de.codecentric.reedelk.module.descriptor.model.ModuleDescriptor;
import de.codecentric.reedelk.plugin.executor.AsyncProgressTask;
import de.codecentric.reedelk.runtime.component.Stop;
import org.jetbrains.annotations.NotNull;

public class LoadFlowControlModuleDescriptor implements AsyncProgressTask {

    private final Callback<Void> onDone;
    private final Callback<ModuleDescriptor> onReady;
    private final ModuleDescriptorAnalyzer moduleAnalyzer = new ModuleDescriptorAnalyzer();

    public LoadFlowControlModuleDescriptor(Callback<Void> onDone, Callback<ModuleDescriptor> onReady) {
        this.onDone = onDone;
        this.onReady = onReady;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        ModuleDescriptor descriptor = moduleAnalyzer.from(Stop.class);
        onReady.execute(descriptor);
        onDone.execute(null);
    }
}
