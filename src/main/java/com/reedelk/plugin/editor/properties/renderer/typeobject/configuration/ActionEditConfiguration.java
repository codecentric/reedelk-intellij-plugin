package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

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

    ActionEditConfiguration(@NotNull Module module,
                            @NotNull TypeObjectDescriptor typeDescriptor) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    protected void onClick(@NotNull ConfigMetadata selectedMetadata) {
        if (selectedMetadata.isEditable()) {
            DialogEditConfiguration dialogEditConfiguration = new DialogEditConfiguration(module, typeDescriptor, selectedMetadata);
            if (dialogEditConfiguration.showAndGet()) {
                ConfigService.getInstance(module).saveConfig(selectedMetadata);
            }
        }
    }

    @Override
    public void onSelect(ConfigMetadata configMetadata) {
        super.onSelect(configMetadata);
        setEnabled(configMetadata.isEditable());
    }
}
