package com.esb.plugin.runconfig.module.runprofile;

import com.esb.internal.rest.api.InternalAPI;
import com.esb.internal.rest.api.hotswap.v1.HotSwapPOSTReq;
import com.esb.internal.rest.api.module.v1.ModuleDELETEReq;
import com.esb.internal.rest.api.module.v1.ModulePOSTReq;
import com.esb.plugin.service.application.http.ESBHttpService;
import com.esb.plugin.service.application.http.HttpResponse;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.components.ServiceManager;

import java.io.IOException;

public class RESTModuleService {

    private final String baseUrl;

    public RESTModuleService(String address, int port) {
        this.baseUrl = String.format("http://%s:%d", address, port);
    }

    public void hotSwap(String moduleFile, String resourcesRootDirectory) throws ExecutionException {
        HotSwapPOSTReq req = new HotSwapPOSTReq();
        req.setModuleFilePath(moduleFile);
        req.setResourcesRootDirectory(resourcesRootDirectory);

        String json = InternalAPI.HotSwap.V1.POST.Req.serialize(req);


        HttpResponse response = post(baseUrl + "/hotswap", json);
        if (response.isNotSuccessful()) {
            // TODO: Formalize this response (as JSON)
            throw new ExecutionException(response.getBody());
        }
    }

    public void deploy(String moduleFile) throws ExecutionException {
        ModulePOSTReq req = new ModulePOSTReq();
        req.setModuleFilePath(moduleFile);

        String json = InternalAPI.Module.V1.POST.Req.serialize(req);


        HttpResponse response = post(baseUrl + "/module", json);
        if (response.isNotSuccessful()) {
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
