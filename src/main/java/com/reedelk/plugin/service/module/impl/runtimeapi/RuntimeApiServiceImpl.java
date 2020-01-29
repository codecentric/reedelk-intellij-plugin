package com.reedelk.plugin.service.module.impl.runtimeapi;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.FlowErrorResponse;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.service.module.HttpService;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.plugin.service.module.impl.http.HttpResponse;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.rest.api.InternalAPI;
import com.reedelk.runtime.rest.api.InternalAPI.Module.V1;
import com.reedelk.runtime.rest.api.hotswap.v1.HotSwapPOSTReq;
import com.reedelk.runtime.rest.api.module.v1.ModuleDELETEReq;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import com.reedelk.runtime.rest.api.module.v1.ModulePOSTReq;
import com.reedelk.runtime.rest.api.module.v1.ModulesGETRes;
import okhttp3.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static com.reedelk.plugin.commons.DefaultConstants.RestApi;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeApiServiceImpl implements RuntimeApiService {

    private static final MediaType JSON = MediaType.get(MimeType.APPLICATION_JSON.toString());

    private final Module module;

    public RuntimeApiServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void hotSwap(String moduleJarFile, String resourcesRootDirectory, String address, int port, OperationCallback callback) {
        HotSwapPOSTReq req = new HotSwapPOSTReq();
        req.setModuleFilePath(moduleJarFile);
        req.setResourcesRootDirectory(resourcesRootDirectory);

        String jsonBody = InternalAPI.HotSwap.V1.POST.Req.serialize(req);
        String requestUrl = RestApi.apiURLOf(address, port, RestApi.HOT_SWAP);
        try {
            HttpResponse response = HttpService.getInstance(module).post(requestUrl, jsonBody, JSON);
            if (response.isNotFound()) {
                handleModulePackageNotInstalled(moduleJarFile, address, port, callback);
            } else if (response.isSuccessful()) {
                callback.onSuccess();
            } else {
                handleNotSuccessfulResponse(response, callback);
            }
        } catch (IOException exception) {
            callback.onError(exception);
        }
    }

    @Override
    public void deploy(String moduleFile, String address, int port, OperationCallback callback) {
        ModulePOSTReq req = new ModulePOSTReq();
        req.setModuleFilePath(moduleFile);

        String jsonBody = InternalAPI.Module.V1.POST.Req.serialize(req);
        String requestUrl = RestApi.apiURLOf(address, port, RestApi.MODULE);
        try {
            HttpResponse response = HttpService.getInstance(module).post(requestUrl, jsonBody, JSON);
            if (response.isSuccessful()) {
                callback.onSuccess();
            } else {
                handleNotSuccessfulResponse(response, callback);
            }
        } catch (IOException exception) {
            callback.onError(exception);
        }
    }

    @Override
    public void install(String moduleFile, String address, int port, OperationCallback callback) {
        String requestUrl = RestApi.apiURLOf(address, port, RestApi.MODULE_DEPLOY);
        File file = new File(moduleFile);
        try {
            HttpResponse response = HttpService.getInstance(module).postMultipart(requestUrl, file, "moduleFilePath");
            if (response.isSuccessful()) {
                callback.onSuccess();
            } else {
                handleNotSuccessfulResponse(response, callback);
            }
        } catch (IOException e) {
            callback.onError(e);
        }
    }

    @Override
    public void delete(String moduleFile, String address, int port, OperationCallback callback) {
        ModuleDELETEReq req = new ModuleDELETEReq();
        req.setModuleFilePath(moduleFile);

        String jsonBody = InternalAPI.Module.V1.DELETE.Req.serialize(req);
        String requestUrl = RestApi.apiURLOf(address, port, RestApi.MODULE);
        try {
            HttpResponse response = HttpService.getInstance(module).delete(requestUrl, jsonBody, JSON);
            if (response.isSuccessful()) {
                callback.onSuccess();
            } else {
                handleNotSuccessfulResponse(response, callback);
            }
        } catch (IOException exception) {
            callback.onError(exception);
        }
    }

    @Override
    public Collection<ModuleGETRes> installedModules(String address, int port) {
        String requestUrl = RestApi.apiURLOf(address, port, RestApi.MODULE);
        try {
            HttpResponse response = HttpService.getInstance(module).get(requestUrl);
            if (response.isSuccessful()) {
                ModulesGETRes installedModules = V1.GET.Res.deserialize(response.getBody());
                return installedModules.getModules();
            } else {
                return Collections.emptyList();
            }
        } catch (IOException exception) {
            return Collections.emptyList();
        }
    }

    private void handleModulePackageNotInstalled(String moduleJarFile, String address, int port, OperationCallback callback) {
        // The module we tried to Hot Swap was not installed in the Runtime.
        // The .jar artifact does not exists in the /target folder, therefore we must re-package the module
        // by executing 'maven package' goal or manually creating the jar and then install it in the runtime.
        // IMPORTANT: It is important to re-package the module since the files supposed to be hot swapped are not in the jar yet!
        ModulePackager packager = new ModulePackager(module.getProject(), module.getName(), new ModulePackager.OnModulePackaged() {
            @Override
            public void onDone() {
                // The  Maven package goal was successful. The .jar artifact is in the /target folder and we can
                // deploy the package onto the ESB runtime.
                deploy(moduleJarFile, address, port, callback);
            }

            @Override
            public void onError(Exception exception) {
                // Maven package goal was not successful
                PluginException wrapped = new PluginException(message("module.run.error.maven.goal.package.failed", module.getName()), exception);
                callback.onError(wrapped);
            }
        });
        packager.doPackage();
    }

    private void handleNotSuccessfulResponse(HttpResponse response, OperationCallback callback) {
        IOException exception = FlowErrorResponse.from(response.getBody())
                .map(flowError -> new IOException(flowError.getErrorMessage()))
                .orElse(new IOException(response.getBody()));
        callback.onError(exception);
    }
}
