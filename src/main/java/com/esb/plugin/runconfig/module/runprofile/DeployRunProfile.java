package com.esb.plugin.runconfig.module.runprofile;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.hotswap.v1.HotSwapPOSTReq;
import com.esb.internal.rest.api.module.v1.ModulePOSTReq;
import com.esb.plugin.service.application.http.HttpResponse;
import com.esb.plugin.service.project.filechange.ESBFileChangeService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;

public class DeployRunProfile extends AbstractRunProfile {

    private final String configName;

    public DeployRunProfile(Project project, String moduleName, String configName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
        this.configName = configName;
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {

        if(ESBFileChangeService.getInstance(project).isHotSwap(runtimeConfigName, moduleName)) {
            // Hot swap
            String mavenDirectory = mavenProject.getDirectory();
            Path resourcesRootDirectory = Paths.get(mavenDirectory, "src", "main", "resources");

            HotSwapPOSTReq req = new HotSwapPOSTReq();
            req.setModuleFilePath(moduleFile);
            req.setResourcesRootDirectory(resourcesRootDirectory.toString());
            String json = InternalAPI.HotSwap.V1.POST.Req.serialize(req);

            String url = getAdminUrlFromResourcePath("/hotswap");

            HttpResponse httpResponse = post(url, json);

            if (httpResponse.isSuccessful()) {
                String message = format("Module <b>%s</b> reloaded", moduleName);
                switchToolWindowAndNotifyWithMessage(message);
            } else {
                throw new ExecutionException(httpResponse.getBody());
            }

        } else {

            // Redeploy Module Jar
            ModulePOSTReq req = new ModulePOSTReq();
            req.setModuleFilePath(moduleFile);
            String json = InternalAPI.Module.V1.POST.Req.serialize(req);

            String url = getAdminUrlFromResourcePath("/module");

            HttpResponse post = post(url, json);

            ESBFileChangeService.getInstance(project).unchanged(runtimeConfigName, moduleName);

            String message = format("Module <b>%s</b> updated", moduleName);
            switchToolWindowAndNotifyWithMessage(message);
        }

        return null;
    }


}
