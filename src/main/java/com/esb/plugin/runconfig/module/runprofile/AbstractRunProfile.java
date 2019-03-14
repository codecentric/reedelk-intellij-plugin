package com.esb.plugin.runconfig.module.runprofile;

import com.esb.plugin.utils.ESBModuleUtils;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

abstract class AbstractRunProfile implements RunProfileState {

    protected final Project project;
    protected final String moduleName;

    AbstractRunProfile(final Project project, final String moduleName) {
        this.project = project;
        this.moduleName = moduleName;
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        Optional<MavenProject> optionalMavenProject = ESBModuleUtils.getMavenProject(moduleName, project);
        if (!optionalMavenProject.isPresent()) {
            throw new ExecutionException("Maven project could not be found");
        }

        MavenProject mavenProject = optionalMavenProject.get();

        String targetDir = mavenProject.getBuildDirectory();

        MavenId mavenId = mavenProject.getMavenId();
        String jarName = mavenId.getArtifactId() + "-" + mavenId.getVersion() + ".jar";

        Path finalJarPath = Paths.get(targetDir, jarName);
        String moduleFilePath = "file:" + finalJarPath.toString();

        return execute(mavenProject, moduleFilePath);
    }

    protected abstract ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException;

    protected  <T> T post(String api, String json, Function<String,T> responseMapper) throws ExecutionException {
        HttpPost post = new HttpPost();
        post.setEntity(EntityBuilder
                .create()
                .setText(json)
                .setContentType(APPLICATION_JSON)
                .build());
        return executeHttp(api, post, responseMapper);
    }

    protected <T> T delete(String api, String json, Function<String,T> responseMapper) throws ExecutionException {
        HttpDeleteWithBody delete = new HttpDeleteWithBody();

        delete.setEntity(EntityBuilder
                .create()
                .setText(json)
                .setContentType(APPLICATION_JSON)
                .build());
        return executeHttp(api, delete, responseMapper);
    }

    @NotThreadSafe
    class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        private static final String METHOD_NAME = "DELETE";

        public String getMethod() {
            return METHOD_NAME;
        }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        public HttpDeleteWithBody(final URI uri) {
            super();
            setURI(uri);
        }

        public HttpDeleteWithBody() {
            super();
        }
    }


    protected <T> T executeHttp(String api, HttpRequestBase request, Function<String,T> responseMapper) throws ExecutionException {
        String url = "http://localhost:9988/" + api;
        HttpClient client = HttpClientBuilder.create().build();
        request.setURI(URI.create(url));

        HttpResponse response;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }

        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer result = new StringBuffer();
        String line = "";
        while (true) {
            try {
                if ((line = rd.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            result.append(line);
        }

        return responseMapper.apply(result.toString());
    }

    protected void switchToRunToolWindow() {
        ToolWindowManager
                .getInstance(project)
                .getToolWindow(ToolWindowId.RUN).show(null);
    }

}
