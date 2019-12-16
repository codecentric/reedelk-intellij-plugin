package com.reedelk.plugin.service.module.impl.modulecheckerror;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.NotificationUtils;
import com.reedelk.plugin.commons.RuntimeConsoleURL;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ModuleCheckErrorsService;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;

import java.util.Objects;

import static com.reedelk.plugin.commons.Defaults.DEFAULT_CHECK_ERROR_DELAY_MILLIS;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ModuleCheckErrorsServiceImpl implements ModuleCheckErrorsService {

    private final Module module;

    public ModuleCheckErrorsServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void checkForErrors(String runtimeHostAddress, int runtimeHostPort) {
        PluginExecutors.runWithDelay(module, DEFAULT_CHECK_ERROR_DELAY_MILLIS,
                message("module.check.errors.task.title"),
                indicator -> MavenUtils.getMavenProject(module).ifPresent(mavenProject -> {

                    // We only check for modules matching the current module's artifact i.
                    String artifactId = mavenProject.getMavenId().getArtifactId();

                    runtimeApiService()
                            .installedModules(runtimeHostAddress, runtimeHostPort)
                            .stream()
                            .filter(moduleGETRes -> Objects.equals(artifactId, moduleGETRes.getName()))
                            .findFirst()
                            .ifPresent(runtimeModule -> notifyFromStateIfNeeded(runtimeModule, runtimeHostAddress, runtimeHostPort));
                }));
    }

    private void notifyFromStateIfNeeded(ModuleGETRes moduleRuntime, String runtimeHostAddress, int runtimeHostPort) {
        // TODO: MOVE STATE CONSTANTS INTO REST-API ENUM
        if (moduleRuntime.getState().equals("ERROR")) {
            NotificationUtils.notifyError(
                    message("module.check.errors.module.errors.title"),
                    message("module.check.errors.module.errors.content",
                            RuntimeConsoleURL.from(runtimeHostAddress, runtimeHostPort)));
        }
        if (moduleRuntime.getState().equals("UNRESOLVED")) {
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