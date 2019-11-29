package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import com.reedelk.plugin.service.module.impl.NewConfigMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.reedelk.plugin.commons.Icons.Config.Add;
import static com.reedelk.runtime.commons.JsonParser.Config;

public class ActionAddConfiguration extends ActionableCommandButton {

    private static final String DEFAULT_CONFIG_FILE_NAME = "test_config.fconfig";
    private static final String DEFAULT_NEW_CONFIG_TITLE = "New configuration";

    private final TypeObjectDescriptor typeDescriptor;
    private final Module module;

    ActionAddConfiguration(@NotNull Module module, @NotNull TypeObjectDescriptor typeDescriptor) {
        super("Add", Add, Add);
        this.typeDescriptor = typeDescriptor;
        this.module = module;
    }

    @Override
    protected void onClick(ConfigMetadata selectedMetadata) {
        // We ignore the selected. Create new config object.
        TypeObjectDescriptor.TypeObject configTypeObject = new TypeObjectDescriptor.TypeObject(typeDescriptor.getTypeFullyQualifiedName());
        configTypeObject.set(Config.id(), UUID.randomUUID().toString());
        configTypeObject.set(Config.title(), DEFAULT_NEW_CONFIG_TITLE);

        ConfigMetadata newConfig = new NewConfigMetadata(DEFAULT_CONFIG_FILE_NAME, configTypeObject, typeDescriptor);

        DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor, newConfig);

        if (dialogAddConfiguration.showAndGet()) {
            ConfigService.getInstance(module).addConfig(newConfig);
        }
    }
}
