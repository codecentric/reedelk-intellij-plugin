package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

public interface ModuleSyncService {

    static ModuleSyncService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ModuleSyncService.class);
    }

    void syncInstalledModules(String runtimeHostAddress, int runtimeHostPort);
}
