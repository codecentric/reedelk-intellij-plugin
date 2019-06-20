package com.esb.plugin.service.module.impl;

import org.json.JSONObject;

public class NewConfigMetadata extends ConfigMetadata {

    private String fileName;

    public NewConfigMetadata(String defaultFineName, JSONObject configDefinition) {
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
