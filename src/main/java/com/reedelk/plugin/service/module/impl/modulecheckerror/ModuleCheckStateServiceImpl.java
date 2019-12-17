package com.reedelk.plugin.service.module.impl.modulecheckerror;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.ModuleState;
import com.reedelk.plugin.commons.NotificationUtils;
import com.reedelk.plugin.commons.RuntimeConsoleURL;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ModuleCheckStateService;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.Objects;
import java.util.Optional;

import static com.reedelk.plugin.commons.Defaults.DEFAULT_CHECK_ERROR_DELAY_MILLIS;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ModuleCheckStateServiceImpl implements ModuleCheckStateService {

    private final Module module;

    public ModuleCheckStateServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void checkModuleState(String runtimeHostAddress, int runtimeHostPort) {
        PluginExecutors.runWithDelay(module, DEFAULT_CHECK_ERROR_DELAY_MILLIS,
                message("module.check.errors.task.title"),
                indicator -> internalCheckModuleState(runtimeHostAddress, runtimeHostPort));
    }

    void internalCheckModuleState(String runtimeHostAddress,  int runtimeHostPort) {
        moduleMavenProject().ifPresent(mavenProject -> {
            // We only check for modules matching the current module's artifact i.
            String artifactId = mavenProject.getMavenId().getArtifactId();

            runtimeApiService()
                    .installedModules(runtimeHostAddress, runtimeHostPort)
                    .stream()
                    .filter(runtimeModule -> Objects.equals(artifactId, runtimeModule.getName()))
                    .findFirst()
                    .ifPresent(runtimeModule -> notifyFromStateIfNeeded(runtimeModule, runtimeHostAddress, runtimeHostPort));
        });
    }

    Optional<MavenProject> moduleMavenProject() {
        return MavenUtils.getMavenProject(module);
    }

    // Notify with a Popup if the module state is 'ERROR' or 'UNRESOLVED'.
    void notifyFromStateIfNeeded(ModuleGETRes moduleRuntime, String runtimeHostAddress, int runtimeHostPort) {
        if (ModuleState.ERROR.name().equals(moduleRuntime.getState())) {
            NotificationUtils.notifyError(
                    message("module.check.errors.module.errors.title"),
                    message("module.check.errors.module.errors.content",
                            RuntimeConsoleURL.from(runtimeHostAddress, runtimeHostPort)));
        }
        if (ModuleState.UNRESOLVED.name().equals(moduleRuntime.getState())) {
            NotificationUtils.notifyError(
                    message("module.check.errors.module.unresolved.title"),
                    message("module.check.errors.module.unresolved.content",
                            RuntimeConsoleURL.from(runtimeHostAddress, runtimeHostPort)));
        }
    }

    RuntimeApiService runtimeApiService() {
        return RuntimeApiService.getInstance(module);
    }
}