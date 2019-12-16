package com.reedelk.plugin.service.module.impl.modulecheckerror;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ModuleCheckErrorsService;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;

import java.util.Collection;
import java.util.Objects;

public class ModuleCheckErrorsServiceImpl implements ModuleCheckErrorsService {

    // TODO: Create Utils class for Reedelk Notifications only
    private static final String REEDELK_NOTIFICATION_GROUP_ID = "Reedelk Integration";

    private final Module module;

    public ModuleCheckErrorsServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void checkForErrors(String runtimeHostAddress, int runtimeHostPort) {
        PluginExecutors.runWithDelay(module, 500, indicator -> {
            indicator.setText(String.format("Checking runtime flows for module [%s]", module.getName()));

            Collection<ModuleGETRes> runtimeModules =
                    runtimeApiService().installedModules(runtimeHostAddress, runtimeHostPort);

            indicator.setIndeterminate(true);
            boolean anyUnresolvedOrError = runtimeModules.stream().anyMatch(moduleGETRes ->
                    MavenUtils.getMavenProject(module).map(mavenProject -> {
                        // We only check for modules matching the current module's artifact i.
                        String artifactId = mavenProject.getMavenId().getArtifactId();
                        return Objects.equals(artifactId, moduleGETRes.getName()) &&
                                moduleGETRes.getState().equals("UNRESOLVED") ||
                                moduleGETRes.getState().equals("ERROR");
                    }).orElse(false));

            if (anyUnresolvedOrError) {
                Notification notification =
                        new Notification(REEDELK_NOTIFICATION_GROUP_ID, null, NotificationType.ERROR)
                                .setTitle("Errors in the flow")
                                .setListener(NotificationListener.URL_OPENING_LISTENER)
                                .setContent("Go to <a href=\"http://" + runtimeHostAddress + ":" + runtimeHostPort + "/console\">Reedelk ESB Administration Console</a>");
                Notifications.Bus.notify(notification);
            }
        });
    }

    RuntimeApiService runtimeApiService() {
        return RuntimeApiService.getInstance(module);
    }
}