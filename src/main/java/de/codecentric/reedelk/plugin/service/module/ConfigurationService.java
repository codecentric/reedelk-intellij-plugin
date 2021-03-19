package de.codecentric.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationService {

    static ConfigurationService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ConfigurationService.class);
    }

    void loadConfigurationsBy(ObjectDescriptor typeObjectDescriptor);

    void saveConfiguration(ConfigMetadata updatedConfig);

    void addConfiguration(ConfigMetadata newConfig);

    void removeConfiguration(ConfigMetadata toBeRemovedConfig);
}
