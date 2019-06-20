package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.domain.ComponentDataHolder;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConfigMetadata implements ComponentDataHolder {

    private final JSONObject configDefinition;
    private final String fullyQualifiedName;
    private final String fileName;
    private final String title;
    private final String id;

    public ConfigMetadata(final String fullyQualifiedName, String id, String title, String fileName, JSONObject configDefinition) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.configDefinition = configDefinition;
        this.fileName = fileName;
        this.title = title;
        this.id = id;
    }

    public ConfigMetadata(final String id, final String title) {
        this(null, id, title, null, null);
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(configDefinition.keySet());
    }

    @Override
    public <T> T get(String key) {
        return configDefinition.has(key) ?
                (T) configDefinition.get(key) :
                null;
    }

    @Override
    public void set(String propertyName, Object propertyValue) {
        configDefinition.put(propertyName, propertyValue);
    }
}
