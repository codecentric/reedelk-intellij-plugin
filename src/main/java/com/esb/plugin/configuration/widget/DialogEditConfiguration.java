package com.esb.plugin.configuration.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogEditConfiguration extends DialogWrapper {

    private final TypeObjectDescriptor objectDescriptor;
    private final ConfigMetadata selectedMetadata;
    private final Module module;

    public DialogEditConfiguration(@NotNull Module module, @NotNull TypeObjectDescriptor typeObjectDescriptor, @NotNull ConfigMetadata selectedConfig) {
        super(module.getProject(), false);
        this.objectDescriptor = typeObjectDescriptor;
        this.selectedMetadata = selectedConfig;
        this.module = module;

        setTitle(Labels.DIALOG_TITLE_EDIT_CONFIGURATION);
        setResizable(false);
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, Labels.DIALOG_BTN_SAVE_CONFIGURATION);
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return new ConfigPropertiesPanel(module, selectedMetadata, objectDescriptor, false);
    }

    void save() {
        ConfigService.getInstance(module)
                .saveConfig(selectedMetadata);
    }
}
