package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

public interface ConfigService {

    static ConfigService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ConfigService.class);
    }

    void fetchConfigurationsBy(TypeObjectDescriptor typeObjectDescriptor);

    void saveConfig(ConfigMetadata updatedConfig);

    void addConfig(ConfigMetadata newConfig);

    void removeConfig(ConfigMetadata toBeRemovedConfig);
}
