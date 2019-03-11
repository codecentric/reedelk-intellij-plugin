package com.esb.plugin.service.project;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface ESBProjectRuntimeService {
    static ESBProjectRuntimeService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ESBProjectRuntimeService.class);
    }
}
