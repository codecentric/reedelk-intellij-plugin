package com.reedelk.plugin.service.module.impl.configuration;

import com.reedelk.component.descriptor.ComponentDataHolder;
import com.reedelk.component.descriptor.TypeObjectDescriptor;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ConfigMetadata implements ComponentDataHolder {

    private final String absentFile = StringUtils.EMPTY;
    private final ComponentDataHolder data;
    private final TypeObjectDescriptor configObjectDescriptor;

    // Used for unselected config definition.
    public ConfigMetadata(@NotNull ComponentDataHolder data) {
        this.data = data;
        this.configObjectDescriptor = null;
    }

    public ConfigMetadata(@NotNull ComponentDataHolder data,
                          @NotNull TypeObjectDescriptor configObjectDescriptor) {
        this.data = data;
        this.configObjectDescriptor = configObjectDescriptor;
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(data.keys());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return data.has(key) ? (T) data.get(key) : null;
    }

    @Override
    public void set(String propertyName, Object propertyValue) {
        data.set(propertyName, propertyValue);
    }

    @Override
    public boolean has(String key) {
        return data.has(key);
    }

    public String getId() {
        return data.get(Config.id());
    }

    public String getTitle() {
        return data.get(Config.title());
    }

    public void setTitle(String newTitle) {
        data.set(Config.title(), newTitle);
    }

    public String getFullyQualifiedName() {
        return data.get(Implementor.name());
    }

    public TypeObjectDescriptor getConfigObjectDescriptor() {
        return configObjectDescriptor;
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
        return absentFile;
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
