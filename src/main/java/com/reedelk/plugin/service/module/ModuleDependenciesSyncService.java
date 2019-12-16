package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

public interface ModuleDependenciesSyncService {

    static ModuleDependenciesSyncService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ModuleDependenciesSyncService.class);
    }

    void syncInstalledModules(String runtimeHostAddress, int runtimeHostPort);
}
