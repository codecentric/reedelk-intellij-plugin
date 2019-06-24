package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.domain.TypeObjectDescriptor;

public class NewConfigMetadata extends ConfigMetadata {

    private String fileName;

    public NewConfigMetadata(String defaultFineName, TypeObjectDescriptor.TypeObject configDefinition) {
        super(configDefinition);
        this.fileName = defaultFineName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String newFileName) {
        this.fileName = newFileName;
    }
}
