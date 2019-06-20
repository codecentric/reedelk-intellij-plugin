package com.esb.plugin.service.module.impl;

import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONObject;

public class ExistingConfigMetadata extends ConfigMetadata {

    private VirtualFile file;

    public ExistingConfigMetadata(VirtualFile file, JSONObject configDefinition) {
        super(configDefinition);
        this.file = file;
    }

    @Override
    public String getConfigFile() {
        return file.getPath();
    }

    @Override
    public String getFileName() {
        return file.getName();
    }
}
