package de.codecentric.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import de.codecentric.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SubflowService {
    static SubflowService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, SubflowService.class);
    }

    List<SubflowMetadata> listSubflows();

}
