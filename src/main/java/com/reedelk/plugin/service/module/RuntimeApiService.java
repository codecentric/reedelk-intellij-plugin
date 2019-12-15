package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RuntimeApiService {

    static RuntimeApiService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, RuntimeApiService.class);
    }

    void hotSwap(String moduleFile, String resourcesRootDirectory, String address, int port, OperationCallback callback);

    void deploy(String moduleFile, String address, int port, OperationCallback callback);

    void install(String moduleFile, String address, int port, OperationCallback callback);

    void delete(String moduleFile, String address, int port, OperationCallback callback);

    Collection<ModuleGETRes> installedModules(String address, int port);

    interface OperationCallback {
        void onSuccess();
        void onError(Exception exception);
    }
}
