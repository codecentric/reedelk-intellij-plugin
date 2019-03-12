package com.esb.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

public interface ESBModuleRuntimeService {
    static ESBModuleRuntimeService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ESBModuleRuntimeService.class);
    }
}
