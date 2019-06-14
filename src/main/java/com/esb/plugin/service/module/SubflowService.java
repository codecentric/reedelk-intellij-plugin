package com.esb.plugin.service.module;

import com.esb.plugin.service.module.impl.SubflowMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SubflowService {

    static SubflowService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, SubflowService.class);
    }

    List<SubflowMetadata> listSubflows();
}
