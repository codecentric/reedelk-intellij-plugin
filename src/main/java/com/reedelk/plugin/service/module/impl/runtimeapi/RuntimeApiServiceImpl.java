package com.reedelk.plugin.service.module.impl.runtimeapi;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.FlowErrorResponse;
import com.reedelk.plugin.maven.MavenPackageGoal;
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

import static com.reedelk.plugin.commons.Defaults.RestApi;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeApiServiceImpl implements RuntimeApiService {

    private static final String BASE_ADMIN_CONSOLE_URL_TEMPLATE = "http://%s:%d/api";
    private static final MediaType JSON = MediaType.get(MimeType.APPLICATION_JSON.toString());

    private final Module module;

    public RuntimeApiServiceImpl(Module module) {
        this.module = module;
    }

    private String urlFrom(String address, int port, String path) {
        return String.format(BASE_ADMIN_CONSOLE_URL_TEMPLATE, address, port) + path;
    }

    @Override
    public void hotSwap(String moduleFile, String resourcesRootDirectory, String address, int port, OperationCallback callback) {
        HotSwapPOSTReq req = new HotSwapPOSTReq();
        req.setModuleFilePath(moduleFile);
        req.setResourcesRootDirectory(resourcesRootDirectory);

        String jsonBody = InternalAPI.HotSwap.V1.POST.Req.serialize(req);
        String requestUrl = urlFrom(address, port, RestApi.HOT_SWAP);
        try {
            HttpResponse response = HttpService.getInstance(module).post(requestUrl, jsonBody, JSON);
            if (response.isNotFound()) {
                // The module we tried to Hot Swap was not installed in the Runtime, therefore we must re-package the module
                // by executing 'maven package' goal and then install it in the runtime. It is important to re-package the
                // module since the files supposed to be hot swapped are not in the jar yet.
                MavenPackageGoal packageGoal = new MavenPackageGoal(module.getProject(), module.getName(), result -> {
                    if (!result) {
                        // Maven package goal was not successful
                        IOException exception = new IOException(message("module.run.error.maven.goal.package.failed", module.getName()));
                        callback.onError(exception);
                    } else {
                        // The  Maven package goal was successful. The .jar artifact is in the /target folder and we can
                        // deploy the package onto the ESB runtime.
                        deploy(moduleFile, address, port, callback);
                    }
                });
                packageGoal.execute();
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
        String requestUrl = urlFrom(address, port, RestApi.MODULE);
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
        String requestUrl = urlFrom(address, port, RestApi.MODULE_DEPLOY);
        File file  = new File(moduleFile);
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
        String requestUrl = urlFrom(address, port, RestApi.MODULE);
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
    public Collection<ModuleGETRes> getInstalledModules(String address, int port) {
        String requestUrl = urlFrom(address, port, RestApi.MODULE);
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

    private void handleNotSuccessfulResponse(HttpResponse response, OperationCallback callback) {
        IOException exception = FlowErrorResponse.from(response.getBody())
                .map(flowError -> new IOException(flowError.getErrorMessage()))
                .orElse(new IOException(response.getBody()));
        callback.onError(exception);
    }
}
