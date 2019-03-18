package com.esb.plugin.runconfig.module.runprofile;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.hotswap.v1.HotSwapPOSTReq;
import com.esb.internal.rest.api.hotswap.v1.HotSwapPOSTRes;
import com.esb.internal.rest.api.module.v1.ModulePOSTReq;
import com.esb.internal.rest.api.module.v1.ModulePOSTRes;
import com.esb.plugin.service.application.http.HttpResponse;
import com.esb.plugin.utils.ESBModuleUtils;
import com.esb.plugin.utils.ESBNotification;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.impl.ConsoleViewUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi;
import groovy.ui.ConsoleView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

public class DeployRunProfile extends AbstractRunProfile {


    public DeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {

        if(ESBModuleUtils.isHotSwap(project, moduleName)) {
            // Hot swap
            String mavenDirectory = mavenProject.getDirectory();
            Path resourcesRootDirectory = Paths.get(mavenDirectory, "src", "main", "resources");

            HotSwapPOSTReq req = new HotSwapPOSTReq();
            req.setModuleFilePath(moduleFile);
            req.setResourcesRootDirectory(resourcesRootDirectory.toString());
            String json = InternalAPI.HotSwap.V1.POST.Req.serialize(req);


            String url = String.format("http://localhost:%d/hotswap", port);

            HttpResponse httpResponse = post(url, json);
            int status = httpResponse.getStatus();

            // We try to hotswap the bundle. In case the runtime has been stopped and
            // restarted, the local code has not been changed, and therefore
            // we need to make a normal deploy (hotswap not possible.
            if (status == 404) {
                // Normal Deploy
                deployModule(moduleFile);
                String message = format("Module <b>%s</b> updated", moduleName);
                switchToolWindowAndNotifyWithMessage(message);

            } else if (httpResponse.isSuccessful()) {
                String message = format("Module <b>%s</b> reloaded", moduleName);
                switchToolWindowAndNotifyWithMessage(message);

            } else {
                // Deserialize (InternalAPI.Hotswap.V1.POST.Res::deserialize
                throw new ExecutionException(httpResponse.getBody());
            }

        } else {

            deployModule(moduleFile);

            ESBModuleUtils.unchanged(project, moduleName);

            String message = format("Module <b>%s</b> updated", moduleName);
            switchToolWindowAndNotifyWithMessage(message);
        }

        return null;
    }

    private void deployModule(String moduleFile) throws ExecutionException {
        // Redeploy Module Jar

        ModulePOSTReq req = new ModulePOSTReq();
        req.setModuleFilePath(moduleFile);
        String json = InternalAPI.Module.V1.POST.Req.serialize(req);

        String url = String.format("http://localhost:%d/module", port);

        HttpResponse post = post(url, json);

        ESBModuleUtils.unchanged(project, moduleName);

        String message = format("Module <b>%s</b> updated", moduleName);
        switchToolWindowAndNotifyWithMessage(message);
    }


}
