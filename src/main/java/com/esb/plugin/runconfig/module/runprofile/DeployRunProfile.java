package com.esb.plugin.runconfig.module.runprofile;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.hotswap.v1.HotSwapPOSTReq;
import com.esb.internal.rest.api.hotswap.v1.HotSwapPOSTRes;
import com.esb.internal.rest.api.module.v1.ModulePOSTReq;
import com.esb.internal.rest.api.module.v1.ModulePOSTRes;
import com.esb.plugin.utils.ESBModuleUtils;
import com.esb.plugin.utils.ESBNotification;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DeployRunProfile extends AbstractRunProfile {

    public DeployRunProfile(Project project, String moduleName) {
        super(project, moduleName);
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

            HotSwapPOSTRes response = post("hotswap", json, InternalAPI.HotSwap.V1.POST.Res::deserialize);

            ESBNotification.notifyInfo("Hot swapped module with id: " + response.getModuleId(), project);


        } else {
            // Redeploy Module Jar

            ModulePOSTReq req = new ModulePOSTReq();

            req.setModuleFilePath(moduleFile);

            String json = InternalAPI.Module.V1.POST.Req.serialize(req);

            ModulePOSTRes response = post("module", json, InternalAPI.Module.V1.POST.Res::deserialize);

            ESBNotification.notifyInfo("Updated module with id: " + response.getModuleId(), project);

            ESBModuleUtils.unchanged(project, moduleName);
        }

        switchToRunToolWindow();

        return null;
    }


}
