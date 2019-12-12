package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

public interface RuntimeApi {

    static RuntimeApi getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, RuntimeApi.class);
    }

    void hotSwap(String moduleFile, String resourcesRootDirectory, String address, int port, OperationCallback callback);

    void deploy(String moduleFile, String address, int port, OperationCallback callback);

    void delete(String moduleFile, String address, int port, OperationCallback callback);

    interface OperationCallback {
        void onSuccess();
        void onError(Exception exception);
    }
}
