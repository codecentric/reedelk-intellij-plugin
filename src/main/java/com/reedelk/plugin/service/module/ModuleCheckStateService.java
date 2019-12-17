package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

public interface ModuleCheckStateService {

    static ModuleCheckStateService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ModuleCheckStateService.class);
    }

    void checkModuleState(String runtimeHostAddress, int runtimeHostPort);
}
