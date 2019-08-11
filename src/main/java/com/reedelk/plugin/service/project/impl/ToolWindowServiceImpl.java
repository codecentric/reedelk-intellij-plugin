package com.reedelk.plugin.service.project.impl;

import com.reedelk.plugin.service.project.ToolWindowService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ToolWindowServiceImpl implements ToolWindowService {

    private Map<String, String> runConfigToolWindowMap = new HashMap<>();

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
