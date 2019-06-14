package com.esb.plugin.service.module.impl;

import com.esb.plugin.service.module.SubflowService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SubflowServiceImpl implements SubflowService {

    private final Module module;

    public SubflowServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public List<SubflowMetadata> listSubflows() {
        List<String> subflows = new ArrayList<>();
        ModuleRootManager.getInstance(module).getFileIndex()
                .iterateContent(fileOrDir -> {
                    if (Objects.equals(fileOrDir.getExtension(), "subflow")) {
                        subflows.add(fileOrDir.getName());
                    }
                    return true;
                });
        return Arrays.asList();
    }
}
