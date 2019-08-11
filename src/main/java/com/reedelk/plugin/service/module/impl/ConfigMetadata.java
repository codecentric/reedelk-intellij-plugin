package com.reedelk.plugin.service.module.impl;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ConfigMetadata implements ComponentDataHolder {

    private final String ABSENT_FILE = "";

    private final ComponentDataHolder configDefinition;

    public ConfigMetadata(@NotNull ComponentDataHolder configDefinition) {
        this.configDefinition = configDefinition;
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(configDefinition.keys());
    }

    @Override
    public <T> T get(String key) {
        return configDefinition.has(key) ? (T) configDefinition.get(key) : null;
    }

    @Override
    public void set(String propertyName, Object propertyValue) {
        configDefinition.set(propertyName, propertyValue);
    }

    @Override
    public boolean has(String key) {
        return configDefinition.has(key);
    }

    public String getId() {
        return configDefinition.get(Config.id());
    }

    public String getTitle() {
        return configDefinition.get(Config.title());
    }

    public void setTitle(String newTitle) {
        configDefinition.set(Config.title(), newTitle);
    }

    public String getFullyQualifiedName() {
        return configDefinition.get(Implementor.name());
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

    public boolean isEditable() {
        return false;
    }

    public boolean isRemovable() {
        return false;
    }
}
