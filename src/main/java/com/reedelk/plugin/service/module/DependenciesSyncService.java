package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface DependenciesSyncService {

    static DependenciesSyncService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, DependenciesSyncService.class);
    }

    void syncInstalledModules(String runtimeHostAddress, int runtimeHostPort, Consumer<Void> onDone);
}
