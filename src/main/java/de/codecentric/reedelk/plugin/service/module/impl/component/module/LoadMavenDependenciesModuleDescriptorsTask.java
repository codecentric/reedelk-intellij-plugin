package de.codecentric.reedelk.plugin.service.module.impl.component.module;

import de.codecentric.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import de.codecentric.reedelk.plugin.maven.MavenUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import de.codecentric.reedelk.module.descriptor.ModuleDescriptorException;
import de.codecentric.reedelk.module.descriptor.analyzer.ModuleDescriptorAnalyzer;
import de.codecentric.reedelk.module.descriptor.model.ModuleDescriptor;
import de.codecentric.reedelk.plugin.executor.AsyncProgressTask;
import de.codecentric.reedelk.runtime.commons.ModuleUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArtifactNode;

import java.util.Optional;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.stream.Collectors.toList;

public class LoadMavenDependenciesModuleDescriptorsTask implements AsyncProgressTask {

    private static final Logger LOG = Logger.getInstance(LoadMavenDependenciesModuleDescriptorsTask.class);

    private final Module module;
    private final Callback<Void> onDone;
    private final Callback<ModuleDescriptor> onReady;
    private final ModuleDescriptorAnalyzer moduleAnalyzer = new ModuleDescriptorAnalyzer();

    public LoadMavenDependenciesModuleDescriptorsTask(Module module, Callback<Void> onDone, Callback<ModuleDescriptor> onReady) {
        this.module = module;
        this.onDone = onDone;
        this.onReady = onReady;
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
                    .forEach(jarFilePath -> ModuleUtils.getModuleName(jarFilePath) // We only scan a module if its jar file has a module name.
                            .flatMap(moduleName -> readModuleDescriptorFromJarFile(jarFilePath, moduleName))
                            .ifPresent(onReady::execute));
        });

        onDone.execute(null);
    }

    private Optional<ModuleDescriptor> readModuleDescriptorFromJarFile(String jarFilePath, String moduleName) {
        try {
            return moduleAnalyzer.from(jarFilePath);
        } catch (ModuleDescriptorException exception) {
            String message = message("module.analyze.jar.error", jarFilePath, moduleName, exception.getMessage());
            LOG.error(message, exception);
            return Optional.empty();
        }
    }
}
