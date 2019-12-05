package com.reedelk.plugin.runconfig.module.runprofile;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.FlowErrorResponse;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.maven.MavenPackageGoal;
import com.reedelk.plugin.service.application.HttpResponse;
import com.reedelk.plugin.service.application.HttpService;
import com.reedelk.runtime.rest.api.InternalAPI;
import com.reedelk.runtime.rest.api.hotswap.v1.HotSwapPOSTReq;
import com.reedelk.runtime.rest.api.module.v1.ModuleDELETEReq;
import com.reedelk.runtime.rest.api.module.v1.ModulePOSTReq;

import java.io.IOException;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.lang.String.format;

public class RestService {

    private static final Logger LOG = Logger.getInstance(RestService.class);
    private static final String BASE_ADMIN_CONSOLE_URL_TEMPLATE = "http://%s:%d/api";

    private interface ApiPaths {
        String module = "/module";
        String hotswap = "/hotswap";
    }

    private final Module module;
    private final String baseUrl;
    private final Project project;

    RestService(Project project, Module module, String address, int port) {
        this.baseUrl = String.format(BASE_ADMIN_CONSOLE_URL_TEMPLATE, address, port);
        this.project = project;
        this.module = module;
    }

    void hotSwap(String moduleFile, String resourcesRootDirectory, String runtimeConfigName) throws ExecutionException {
        HotSwapPOSTReq req = new HotSwapPOSTReq();
        req.setModuleFilePath(moduleFile);
        req.setResourcesRootDirectory(resourcesRootDirectory);

        String json = InternalAPI.HotSwap.V1.POST.Req.serialize(req);

        HttpResponse response = post(baseUrl + ApiPaths.hotswap, json);
        if (response.isNotFound()) {
            // The module we tried to Hot Swap was not installed
            // in the Runtime, therefore we must re-package the module
            // by executing 'maven package' goal and then install it in the runtime.
            // It is important to re-package the module since the files supposed to be
            // hot swapped are not in the jar yet.
            MavenPackageGoal packageGoal = new MavenPackageGoal(project, module.getName(), result -> {
                if (!result) {
                    // Maven package goal was not successful
                    LOG.warn(message("module.run.error.maven.goal.package.failed", module.getName()));
                } else {
                    try {
                        // The  Maven package goal was successful. The .jar artifact is in the /target folder
                        // and we can deploy the package onto the ESB runtime.
                        deploy(moduleFile, runtimeConfigName);
                    } catch (ExecutionException e) {
                        LOG.warn(message("module.run.error.maven.goal.package.failed", module.getName()));
                    }
                }
            });
            packageGoal.execute();
        } else if (response.isNotSuccessful()) {
            handleNotSuccessfulResponse(response);
        } else {
            // Successful
            String message = format("Module <b>%s</b> reloaded", module.getName());
            ToolWindowUtils.switchToolWindowAndNotifyWithMessage(project, message, runtimeConfigName);
        }
    }

    void deploy(String moduleFile, String runtimeConfigName) throws ExecutionException {
        ModulePOSTReq req = new ModulePOSTReq();
        req.setModuleFilePath(moduleFile);
        String json = InternalAPI.Module.V1.POST.Req.serialize(req);
        HttpResponse response = post(baseUrl + ApiPaths.module, json);
        if (response.isNotSuccessful()) {
            handleNotSuccessfulResponse(response);
        } else {
            String message = format("Module <b>%s</b> updated", module.getName());
            ToolWindowUtils.switchToolWindowAndNotifyWithMessage(module.getProject(), message, runtimeConfigName);
        }
    }

    public void delete(String moduleFile, String runtimeConfigName) throws ExecutionException {
        ModuleDELETEReq req = new ModuleDELETEReq();
        req.setModuleFilePath(moduleFile);
        String json = InternalAPI.Module.V1.DELETE.Req.serialize(req);
        HttpResponse response = _delete(baseUrl + ApiPaths.module, json);
        if (response.isNotSuccessful()) {
            handleNotSuccessfulResponse(response);
        } else {
            String message = format("Module <b>%s</b> uninstalled", module.getName());
            ToolWindowUtils.switchToolWindowAndNotifyWithMessage(module.getProject(), message, runtimeConfigName);
        }
    }

    private HttpResponse post(String url, String json) throws ExecutionException {
        HttpService HttpService = ServiceManager.getService(HttpService.class);
        try {
            return HttpService.post(url, json, HttpService.JSON);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private HttpResponse _delete(String url, String json) throws ExecutionException {
        HttpService HttpService = ServiceManager.getService(HttpService.class);
        try {
            return HttpService.delete(url, json, HttpService.JSON);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private void handleNotSuccessfulResponse(HttpResponse response) throws ExecutionException {
        LOG.warn(response.getBody());
        throw FlowErrorResponse.from(response.getBody())
                .map(flowError -> new ExecutionException(flowError.getErrorMessage()))
                .orElse(new ExecutionException(response.getBody()));
    }
}
