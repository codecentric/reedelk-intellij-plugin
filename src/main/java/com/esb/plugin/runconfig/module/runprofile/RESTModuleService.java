package com.esb.plugin.runconfig.module.runprofile;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.hotswap.v1.HotSwapPOSTReq;
import com.esb.internal.rest.api.module.v1.ModuleDELETEReq;
import com.esb.internal.rest.api.module.v1.ModulePOSTReq;
import com.esb.plugin.maven.MavenPackageGoal;
import com.esb.plugin.service.application.http.ESBHttpService;
import com.esb.plugin.service.application.http.HttpResponse;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import java.io.IOException;

public class RESTModuleService {

    private static final Logger LOG = Logger.getInstance(RESTModuleService.class);

    private static final String BASE_ADMIN_CONSOLE_URL_TEMPLATE = "http://%s:%d/api";
    private final String baseUrl;
    private final Project project;
    private final Module module;

    RESTModuleService(Project project, Module module, String address, int port) {
        this.baseUrl = String.format(BASE_ADMIN_CONSOLE_URL_TEMPLATE, address, port);
        this.project = project;
        this.module = module;
    }

    void hotSwap(String moduleFile, String resourcesRootDirectory) throws ExecutionException {
        HotSwapPOSTReq req = new HotSwapPOSTReq();
        req.setModuleFilePath(moduleFile);
        req.setResourcesRootDirectory(resourcesRootDirectory);

        String json = InternalAPI.HotSwap.V1.POST.Req.serialize(req);


        HttpResponse response = post(baseUrl + "/hotswap", json);
        if (response.isNotFound()) {
            // The module we tried to Hot Swap was not installed
            // in the Runtime, therefore we must re-package the module
            // by executing 'maven package' goal and then install it in the runtime.
            // It is important to re-package the module since the files supposed to be
            // hot swapped are not in the jar yet.
            MavenPackageGoal packageGoal = new MavenPackageGoal(project, module.getName(), result -> {
                try {
                    if (result) deploy(moduleFile);
                    else LOG.error("Deploy error", "Maven goal 'package' failed");
                } catch (ExecutionException e) {
                    LOG.error("Deploy error", e);
                }
            });

            packageGoal.execute();

        } else if (response.isNotSuccessful()) {
            LOG.error("HotSwap error", response.getBody());
            throw new ExecutionException(response.getBody());
        }
    }

    void deploy(String moduleFile) throws ExecutionException {
        ModulePOSTReq req = new ModulePOSTReq();
        req.setModuleFilePath(moduleFile);

        String json = InternalAPI.Module.V1.POST.Req.serialize(req);


        HttpResponse response = post(baseUrl + "/module", json);
        if (response.isNotSuccessful()) {
            LOG.error("Deploy error", response.getBody());

            // TODO: Formalize this response (as JSON)
            throw new ExecutionException(response.getBody());
        }
    }

    public void delete(String moduleFile) throws ExecutionException {
        ModuleDELETEReq req = new ModuleDELETEReq();

        req.setModuleFilePath(moduleFile);

        String json = InternalAPI.Module.V1.DELETE.Req.serialize(req);

        HttpResponse response = delete(baseUrl + "/module", json);
        if (response.isNotSuccessful()) {
            LOG.error("Delete error", response.getBody());

            // TODO: Formalize this response (as JSON)
            throw new ExecutionException(response.getBody());
        }
    }

    private HttpResponse post(String url, String json) throws ExecutionException {
        ESBHttpService ESBHttpService = ServiceManager.getService(ESBHttpService.class);
        try {
            return ESBHttpService.post(url, json, ESBHttpService.JSON);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private HttpResponse delete(String url, String json) throws ExecutionException {
        ESBHttpService ESBHttpService = ServiceManager.getService(ESBHttpService.class);
        try {
            return ESBHttpService.delete(url, json, ESBHttpService.JSON);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }
}
