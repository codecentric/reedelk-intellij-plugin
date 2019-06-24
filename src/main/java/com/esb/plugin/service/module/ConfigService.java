package com.esb.plugin.service.module;

import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConfigService {

    static ConfigService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ConfigService.class);
    }

    List<ConfigMetadata> listConfigsBy(String fullyQualifiedName);

    void saveConfig(ConfigMetadata updatedConfig);

    void addConfig(ConfigMetadata newConfig);

    void removeConfig(ConfigMetadata toBeRemovedConfig);
}