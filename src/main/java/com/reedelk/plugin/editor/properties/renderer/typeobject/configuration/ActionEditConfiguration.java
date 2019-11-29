package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Edit;
import static com.reedelk.plugin.commons.Icons.Config.EditDisabled;

public class ActionEditConfiguration extends ClickableLabel {

    private ConfigMetadata selected;

    private final Module module;
    private final TypeObjectDescriptor typeDescriptor;

    ActionEditConfiguration(@NotNull Module module,
                            @NotNull TypeObjectDescriptor typeDescriptor) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (selected.isEditable()) {
            DialogEditConfiguration dialogEditConfiguration = new DialogEditConfiguration(module, typeDescriptor, selected);
            if (dialogEditConfiguration.showAndGet()) {
                ConfigService.getInstance(module).saveConfig(selected);
            }
        }
    }

    void onSelect(ConfigMetadata selected) {
        this.selected = selected;
        setEnabled(this.selected.isEditable());
    }
}
