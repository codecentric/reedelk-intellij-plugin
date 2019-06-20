package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConfigMetadata implements ComponentDataHolder {

    private final JSONObject configDefinition;
    private final String fullyQualifiedName;
    private final VirtualFile virtualFile;
    private final String title;
    private final String id;

    public ConfigMetadata(final String fullyQualifiedName, String id, String title, VirtualFile virtualFile, JSONObject configDefinition) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.configDefinition = configDefinition;
        this.virtualFile = virtualFile;
        this.title = title;
        this.id = id;
    }

    public ConfigMetadata(final String id, final String title) {
        this(null, id, title, null, null);
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(configDefinition.keySet());
    }

    @Override
    public <T> T get(String key) {
        return configDefinition.has(key) ? (T) configDefinition.get(key) : null;
    }

    @Override
    public void set(String propertyName, Object propertyValue) {
        configDefinition.put(propertyName, propertyValue);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return virtualFile.getName();
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
