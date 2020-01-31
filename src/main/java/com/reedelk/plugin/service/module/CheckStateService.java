package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

public interface CheckStateService {

    static CheckStateService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, CheckStateService.class);
    }

    void checkModuleState(String runtimeHostAddress, int runtimeHostPort);

    boolean isModuleUnresolved(String runtimeHostAddress, int runtimeHostPort);
}
