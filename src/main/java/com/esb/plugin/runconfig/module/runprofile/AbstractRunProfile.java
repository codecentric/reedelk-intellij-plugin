package com.esb.plugin.runconfig.module.runprofile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.function.Function;

abstract class AbstractRunProfile implements RunProfileState {

    protected final Project project;
    protected final String moduleName;

    AbstractRunProfile(final Project project, final String moduleName) {
        this.project = project;
        this.moduleName = moduleName;
    }


    protected <T> T executeHttp(HttpRequestBase request, Function<String,T> responseMapper) throws ExecutionException {
        String url = "http://localhost:9988/module";
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
