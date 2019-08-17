package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public interface ConfigService {

    static ConfigService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ConfigService.class);
    }

    List<ConfigMetadata> listConfigsBy(ComponentPropertyDescriptor configPropertyDescriptor);

    void saveConfig(ConfigMetadata updatedConfig) throws Exception;

    void addConfig(ConfigMetadata newConfig) throws IOException;

    void removeConfig(ConfigMetadata toBeRemovedConfig) throws Exception;
}
