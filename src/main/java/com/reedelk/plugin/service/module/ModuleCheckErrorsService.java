package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

public interface ModuleCheckErrorsService {

    static ModuleCheckErrorsService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ModuleCheckErrorsService.class);
    }

    void checkForErrors(String runtimeHostAddress, int runtimeHostPort);
}
