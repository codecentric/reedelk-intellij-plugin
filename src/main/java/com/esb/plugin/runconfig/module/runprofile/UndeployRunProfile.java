package com.esb.plugin.runconfig.module.runprofile;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.module.v1.ModuleDELETEReq;
import com.esb.internal.rest.api.module.v1.ModuleDELETERes;
import com.esb.plugin.utils.ESBModuleUtils;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import static java.lang.String.format;

public class UndeployRunProfile extends AbstractRunProfile {


    public UndeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {

        // Un Deploy Module

        ModuleDELETEReq req = new ModuleDELETEReq();

        req.setModuleFilePath(moduleFile);

        String json = InternalAPI.Module.V1.DELETE.Req.serialize(req);

        // TODO: do something with this response
        ModuleDELETERes response = delete("module", json, InternalAPI.Module.V1.DELETE.Res::deserialize);

        ESBModuleUtils.changed(project, moduleName);

        String message = format("Module <b>%s</b> uninstalled", moduleName);
        switchToolWindowAndNotifyWithMessage(message);

        return null;

    }

}
