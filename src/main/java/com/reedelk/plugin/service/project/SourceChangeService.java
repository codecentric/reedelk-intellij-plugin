package com.reedelk.plugin.service.project;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface SourceChangeService {

    static SourceChangeService getInstance(@NotNull Project project) {
        return project.getComponent(SourceChangeService.class);
    }

    boolean isHotSwap(String runtimeConfigName, String moduleName);

    boolean isCompileRequired(String runtimeConfigName, String moduleName);

    void unchanged(String runtimeConfigName, String moduleName);

    void changed(String runtimeConfigName, String moduleName);

    void reset(String runtimeConfigName);

}
