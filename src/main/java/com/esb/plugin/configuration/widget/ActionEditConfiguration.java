package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import static com.esb.plugin.commons.Icons.Config.Edit;
import static com.esb.plugin.commons.Icons.Config.EditDisabled;

public class ActionEditConfiguration extends ActionableCommandButton {

    private final Module module;
    private final TypeObjectDescriptor typeDescriptor;


    public ActionEditConfiguration(@NotNull Module module, @NotNull TypeObjectDescriptor typeDescriptor) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    protected void onClick(@NotNull ConfigMetadata selectedMetadata) {
        if (!selectedMetadata.isEditable()) return;

        DialogEditConfiguration dialogEditConfiguration = new DialogEditConfiguration(module, typeDescriptor, selectedMetadata);

        if (dialogEditConfiguration.showAndGet()) {

            dialogEditConfiguration.save();

        }
    }

    @Override
    public void onSelect(ConfigMetadata configMetadata) {
        super.onSelect(configMetadata);
        setEnabled(configMetadata.isEditable());
    }
}
