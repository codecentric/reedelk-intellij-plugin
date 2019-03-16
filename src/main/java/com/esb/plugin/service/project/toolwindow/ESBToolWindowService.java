package com.esb.plugin.service.project.toolwindow;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This service keeps track of the tool window id for a given run config
 */
public interface ESBToolWindowService {
    static ESBToolWindowService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ESBToolWindowService.class);
    }

    void put(String runConfigName, String toolWindowId);

    Optional<String> get(String runConfigName);
}
