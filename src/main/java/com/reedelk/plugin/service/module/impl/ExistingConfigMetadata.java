package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.component.domain.JsonObjectComponentHolderDecorator;
import org.json.JSONObject;

public class ExistingConfigMetadata extends ConfigMetadata {

    private VirtualFile file;

    public ExistingConfigMetadata(VirtualFile file, JSONObject configDefinition) {
        super(new JsonObjectComponentHolderDecorator(configDefinition));
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

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isRemovable() {
        return true;
    }
}
