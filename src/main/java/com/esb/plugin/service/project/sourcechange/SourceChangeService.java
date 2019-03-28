package com.esb.plugin.service.project.sourcechange;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface SourceChangeService {

    static SourceChangeService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, SourceChangeService.class);
    }

    boolean isHotSwap(String runtimeConfigName, String moduleName);

    boolean isCompileRequired(String runtimeConfigName, String moduleName);

    void unchanged(String runtimeConfigName, String moduleName);

    void changed(String runtimeConfigName, String moduleName);

    void reset(String runtimeConfigName);

}
