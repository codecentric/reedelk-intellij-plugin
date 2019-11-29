package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class DialogEditConfiguration extends DialogWrapper {

    private final boolean EXISTING_CONFIG = false;

    private final TypeObjectDescriptor objectDescriptor;
    private final ConfigMetadata selectedMetadata;
    private final Module module;

    DialogEditConfiguration(@NotNull Module module,
                            @NotNull TypeObjectDescriptor typeObjectDescriptor,
                            @NotNull ConfigMetadata selectedConfig) {
        super(module.getProject(), false);
        this.objectDescriptor = typeObjectDescriptor;
        this.selectedMetadata = selectedConfig;
        this.module = module;

        setTitle(message("config.dialog.edit.title"));
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, message("config.dialog.edit.btn.edit"));
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        ConfigPropertiesPanel panel = new ConfigPropertiesPanel(module, selectedMetadata, objectDescriptor, EXISTING_CONFIG);
        return ContainerFactory.makeItScrollable(panel);
    }
}
