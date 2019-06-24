package com.esb.plugin.configuration.widget;

import com.esb.plugin.commons.JsonObjectFactory;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.esb.plugin.service.module.impl.NewConfigMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

import static com.esb.internal.commons.JsonParser.Config;
import static com.esb.plugin.commons.Icons.Config.Add;

public class ActionAddConfiguration extends ActionableCommandButton {

    private static final String DEFAULT_CONFIG_FILE_NAME = "test_config.fconfig";
    private static final String DEFAULT_NEW_CONFIG_TITLE = "New configuration";

    private final TypeObjectDescriptor typeDescriptor;
    private final Module module;

    private AddCompleteListener listener;

    public ActionAddConfiguration(@NotNull Module module, @NotNull TypeObjectDescriptor typeDescriptor) {
        super("Add", Add, Add);
        this.typeDescriptor = typeDescriptor;
        this.module = module;
    }

    @Override
    protected void onClick(ConfigMetadata selectedMetadata) {
        // We ignore the selected. Create new config object.

        TypeObjectDescriptor.TypeObject configTypeObject = new TypeObjectDescriptor.TypeObject(typeDescriptor.getTypeFullyQualifiedName());
        JsonObjectFactory.newJSONObject();

        configTypeObject.set(Config.id(), UUID.randomUUID().toString());
        configTypeObject.set(Config.title(), DEFAULT_NEW_CONFIG_TITLE);


        ConfigMetadata newConfig = new NewConfigMetadata(DEFAULT_CONFIG_FILE_NAME, configTypeObject);

        DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor, newConfig);

        if (dialogAddConfiguration.showAndGet()) {
            try {
                ConfigService.getInstance(module).addConfig(newConfig);
                if (listener != null) {
                    listener.onAddedConfiguration(newConfig);
                }
            } catch (IOException exception) {
                if (listener != null) {
                    listener.onAddedConfigurationError(exception, newConfig);
                }
            }
        }
    }

    public void addListener(AddCompleteListener listener) {
        this.listener = listener;
    }

    public interface AddCompleteListener {
        void onAddedConfiguration(ConfigMetadata metadata);

        void onAddedConfigurationError(Exception exception, ConfigMetadata metadata);
    }
}
