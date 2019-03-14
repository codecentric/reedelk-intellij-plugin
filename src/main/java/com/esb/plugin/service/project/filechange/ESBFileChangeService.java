package com.esb.plugin.service.project.filechange;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface ESBFileChangeService {

    static ESBFileChangeService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ESBFileChangeService.class);
    }

    boolean isCompileRequiredAndSetUnchanged(String moduleName);
}
