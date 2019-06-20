package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.domain.ComponentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.esb.internal.commons.JsonParser.Config;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ConfigMetadata implements ComponentDataHolder {

    private final String ABSENT_FILE = "";

    private final JSONObject configDefinition;

    public ConfigMetadata(@NotNull JSONObject configDefinition) {
        this.configDefinition = configDefinition;
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
        return configDefinition.getString(Config.id());
    }

    public String getTitle() {
        return configDefinition.getString(Config.title());
    }

    public void setTitle(String newTitle) {
        configDefinition.put(Config.title(), newTitle);
    }

    public String getFullyQualifiedName() {
        return configDefinition.getString(Implementor.name());
    }

    /**
     * Full path of the config file.
     */
    public String getConfigFile() {
        throw new UnsupportedOperationException("Could not get config file");
    }

    /**
     * Returns a user friendly version of the file name.
     */
    public String getFileName() {
        return ABSENT_FILE;
    }

    /**
     * Sets only the file name.
     */
    public void setFileName(String newFileName) {
        throw new UnsupportedOperationException("Could not set config file name");
    }

}
