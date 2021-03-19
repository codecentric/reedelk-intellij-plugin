package de.codecentric.reedelk.plugin.service.module.impl.component.module;

import de.codecentric.reedelk.plugin.maven.MavenUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.roots.ModuleRootManager;
import de.codecentric.reedelk.module.descriptor.ModuleDescriptorException;
import de.codecentric.reedelk.module.descriptor.analyzer.ModuleDescriptorAnalyzer;
import de.codecentric.reedelk.module.descriptor.model.ModuleDescriptor;
import de.codecentric.reedelk.plugin.executor.AsyncProgressTask;
import org.jetbrains.annotations.NotNull;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Arrays.stream;

public class LoadModuleCustomComponentModuleDescriptor implements AsyncProgressTask {

    private static final Logger LOG = Logger.getInstance(LoadModuleCustomComponentModuleDescriptor.class);

    private final Module module;
    private final Callback<ModuleDescriptor> onReady;

    public LoadModuleCustomComponentModuleDescriptor(Module module, Callback<ModuleDescriptor> onReady) {
        this.module = module;
        this.onReady = onReady;
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
                        onReady.execute(packageComponents);
                    } catch (ModuleDescriptorException exception) {
                        String message = message("module.analyze.error", module.getName(), exception.getMessage());
                        LOG.error(message, exception);
                    }
                }));
    }
}
