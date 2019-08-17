package com.reedelk.plugin.configuration.widget;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.commons.Icons.Config.Edit;
import static com.reedelk.plugin.commons.Icons.Config.EditDisabled;

public class ActionEditConfiguration extends ActionableCommandButton {

    private final Module module;
    private final TypeObjectDescriptor typeDescriptor;

    private EditCompleteListener listener;

    ActionEditConfiguration(@NotNull Module module,
                            @NotNull TypeObjectDescriptor typeDescriptor) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    protected void onClick(@NotNull ConfigMetadata selectedMetadata) {
        if (!selectedMetadata.isEditable()) return;

        DialogEditConfiguration dialogEditConfiguration = new DialogEditConfiguration(module, typeDescriptor, selectedMetadata);
        if (dialogEditConfiguration.showAndGet()) {
            try {
                ConfigService.getInstance(module).saveConfig(selectedMetadata);
            } catch (Exception exception) {
                if (listener != null) {
                    listener.onEditConfigurationError(exception, selectedMetadata);
                }
            }
        }
    }

    @Override
    public void onSelect(ConfigMetadata configMetadata) {
        super.onSelect(configMetadata);
        setEnabled(configMetadata.isEditable());
    }

    public void addListener(EditCompleteListener listener) {
        this.listener = listener;
    }

    public interface EditCompleteListener {
        void onEditConfigurationError(Exception exception, ConfigMetadata metadata);
    }
}
