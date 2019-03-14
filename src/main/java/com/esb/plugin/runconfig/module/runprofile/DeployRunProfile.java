package com.esb.plugin.runconfig.module.runprofile;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.module.v1.ModulePOSTReq;
import com.esb.internal.rest.api.module.v1.ModulePOSTRes;
import com.esb.plugin.utils.ESBModuleUtils;
import com.esb.plugin.utils.ESBNotification;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class DeployRunProfile extends AbstractRunProfile {

    public DeployRunProfile(Project project, String moduleName) {
        super(project, moduleName);
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {

        Optional<MavenProject> optionalMavenProject = ESBModuleUtils.getMavenProject(moduleName, project);
        if (!optionalMavenProject.isPresent()) {
            throw new ExecutionException("Maven project could not be found");
        }

        MavenProject mavenProject = optionalMavenProject.get();

        if(ESBModuleUtils.isHotSwap(project, moduleName)) {
            // Hot swap

            String mavenDirectory = mavenProject.getDirectory();
            System.out.println(mavenDirectory);

        } else {
            // Redeploy Bundle (including classes)

            String targetDir = mavenProject.getBuildDirectory();

            MavenId mavenId = mavenProject.getMavenId();
            String jarName = mavenId.getArtifactId() + "-" + mavenId.getVersion() + ".jar";

            Path finalJarPath = Paths.get(targetDir, jarName);

            ModulePOSTReq deployRequest = new ModulePOSTReq();
            deployRequest.setModuleFilePath("file:" + finalJarPath.toString());

            ModulePOSTRes response = deploy(deployRequest);

            switchToRunToolWindow();

            ESBNotification.notifyInfo("Updated Module with ID: " + response.getModuleId(), project);

            ESBModuleUtils.unchanged(project, moduleName);

        }


        return null;
    }

    private ModulePOSTRes deploy(ModulePOSTReq request) throws ExecutionException {
        String json = InternalAPI.Module.V1.POST.Req.serialize(request);

        HttpPost post = new HttpPost();
        post.setEntity(EntityBuilder
                .create()
                .setText(json)
                .setContentType(ContentType.APPLICATION_JSON)
                .build());

        return executeHttp(post, InternalAPI.Module.V1.POST.Res::deserialize);
    }

}
