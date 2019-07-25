package com.esb.plugin.service.project.impl;

import com.esb.plugin.service.project.ToolWindowService;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ToolWindowServiceImpl implements ToolWindowService {

    private Map<String,String> runConfigToolWindowMap = new HashMap<>();

    public ToolWindowServiceImpl(Project project) {
    }

    @Override
    public void put(String runConfigName, String toolWindowId) {
        this.runConfigToolWindowMap.put(runConfigName, toolWindowId);
    }

    @Override
    public Optional<String> get(String runConfigName) {
        if (runConfigToolWindowMap.containsKey(runConfigName)) {
            return Optional.of(runConfigToolWindowMap.get(runConfigName));
        }
        return Optional.empty();
    }
}
