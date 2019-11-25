package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ScriptService {
    static ScriptService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ScriptService.class);
    }

    List<String> listAllScripts();
}
