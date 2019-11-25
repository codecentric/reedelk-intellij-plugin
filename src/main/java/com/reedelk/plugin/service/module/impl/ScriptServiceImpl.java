package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.runtime.commons.FileExtension;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.commons.ModuleProperties.Script;

public class ScriptServiceImpl implements ScriptService {

    private final Module module;

    public ScriptServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public List<String> listAllScripts() {

        String scriptsFolder = ModuleUtils.getResourcesFolder(module)
                .map(resourcesFolder -> resourcesFolder + Script.RESOURCE_DIRECTORY)
                .orElseThrow(() -> new IllegalStateException("The project must have a resource folder defined in the project."));

        List<String> scripts = new ArrayList<>();
        ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
            if (FileExtension.SCRIPT.value().equals(fileOrDir.getExtension())) {
                if (fileOrDir.getPresentableUrl().startsWith(scriptsFolder)) {
                    String substring = fileOrDir.getPresentableUrl().substring(scriptsFolder.length() + 1);
                    scripts.add(substring);
                }
            }
            return true;
        });
        return scripts;
    }
}
