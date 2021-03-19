package de.codecentric.reedelk.plugin.service.module.impl.checkstate;

import de.codecentric.reedelk.plugin.commons.DefaultConstants;
import de.codecentric.reedelk.plugin.commons.ModuleState;
import de.codecentric.reedelk.plugin.commons.NotificationUtils;
import de.codecentric.reedelk.plugin.commons.RuntimeConsoleURL;
import de.codecentric.reedelk.plugin.maven.MavenUtils;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import de.codecentric.reedelk.plugin.service.module.CheckStateService;
import de.codecentric.reedelk.plugin.service.module.RuntimeApiService;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.executor.PluginExecutors;
import de.codecentric.reedelk.runtime.api.commons.FlowError;
import de.codecentric.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class CheckStateServiceImpl implements CheckStateService {

    private final Module module;

    public CheckStateServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void checkModuleState(String runtimeHostAddress, int runtimeHostPort) {
        PluginExecutors.runWithDelay(module, DefaultConstants.DEFAULT_CHECK_ERROR_DELAY_MILLIS,
                ReedelkBundle.message("module.check.errors.task.title"),
                indicator -> internalCheckModuleState(runtimeHostAddress, runtimeHostPort));
    }

    @Override
    public boolean isModuleUnresolved(String runtimeHostAddress, int runtimeHostPort) {
        return moduleMavenProject().flatMap(mavenProject ->
                Optional.ofNullable(mavenProject.getMavenId().getArtifactId())
                        .flatMap(artifactId -> runtimeApiService()
                                .installedModules(runtimeHostAddress, runtimeHostPort)
                                .stream()
                                .filter(runtimeModule -> Objects.equals(artifactId, runtimeModule.getName()))
                                .findFirst()))
                .map(moduleGETRes -> ModuleState.UNRESOLVED.name().equals(moduleGETRes.getState()))
                .orElse(false);
    }

    void internalCheckModuleState(String runtimeHostAddress, int runtimeHostPort) {
        // We only check for modules matching the current module's artifact id.
        moduleMavenProject().flatMap(mavenProject ->
                Optional.ofNullable(mavenProject.getMavenId().getArtifactId()).flatMap(artifactId ->
                        runtimeApiService()
                                .installedModules(runtimeHostAddress, runtimeHostPort)
                                .stream()
                                .filter(runtimeModule -> Objects.equals(artifactId, runtimeModule.getName()))
                                .findFirst())).ifPresent(runtimeModule -> notifyFromStateIfNeeded(runtimeModule, runtimeHostAddress, runtimeHostPort));
    }

    // Notify with a Popup if the module state is 'ERROR' or 'UNRESOLVED'.
    void notifyFromStateIfNeeded(ModuleGETRes moduleRuntime, String runtimeHostAddress, int runtimeHostPort) {
        if (ModuleState.ERROR.name().equals(moduleRuntime.getState())) {
            String errorMessages = extractErrorMessages(moduleRuntime);

            NotificationUtils.notifyError(
                    message("module.check.errors.module.errors.title", moduleRuntime.getName()),
                    ReedelkBundle.message("module.check.errors.module.errors.content",
                            RuntimeConsoleURL.from(runtimeHostAddress, runtimeHostPort), errorMessages));
        }
        if (ModuleState.UNRESOLVED.name().equals(moduleRuntime.getState())) {
            NotificationUtils.notifyError(
                    message("module.check.errors.module.unresolved.title", moduleRuntime.getName()),
                    ReedelkBundle.message("module.check.errors.module.unresolved.content",
                            RuntimeConsoleURL.from(runtimeHostAddress, runtimeHostPort)));
        }
    }

    Optional<MavenProject> moduleMavenProject() {
        return MavenUtils.getMavenProject(module);
    }

    RuntimeApiService runtimeApiService() {
        return RuntimeApiService.getInstance(module);
    }

    /**
     * Extracts error messages from the module GET Response. If the error message has the following JSON structure,
     * which is the default error message structure of flow errors, then the 'errorMessage' JSON property is extracted,
     * otherwise the original error message is used.
     * {
     *  "moduleName": "...",
     *  "errorMessage": "...",
     *  "flowTitle": "...",
     *  "moduleId": ...,
     *  "flowId": "...",
     *  "errorType": "..."
     * }
     */
    @NotNull
    private String extractErrorMessages(ModuleGETRes moduleRuntime) {
        return moduleRuntime.getErrors()
                .stream()
                .map(errorGETRes -> {
                    try {
                        // Try to deserialize JSON error message.
                        // If it is not a JSON structure, we return the original message.
                        JSONObject error = new JSONObject(errorGETRes.getMessage());
                        return error.has(FlowError.Properties.errorMessage) ?
                                error.getString(FlowError.Properties.errorMessage) :
                                errorGETRes.getMessage();
                    } catch (Exception ignored) {
                        return errorGETRes.getMessage();
                    }
                })
                .map(error -> "<i>" + error + "</i>") // We make it italic
                .collect(Collectors.joining(", "));
    }
}
