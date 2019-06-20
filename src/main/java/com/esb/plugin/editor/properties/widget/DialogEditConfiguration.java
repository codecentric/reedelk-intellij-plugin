package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogEditConfiguration extends DialogWrapper {

    private final TypeObjectDescriptor objectDescriptor;
    private final Module module;
    private ConfigMetadata selectedMetadata;

    public DialogEditConfiguration(@NotNull Module module, @NotNull TypeObjectDescriptor typeObjectDescriptor, @NotNull ConfigMetadata selectedConfig) {
        super(module.getProject(), false);
        this.objectDescriptor = typeObjectDescriptor;
        this.module = module;
        this.selectedMetadata = selectedConfig;
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
        return new EditConfigPropertiesPanel(module, selectedMetadata, objectDescriptor);
    }

    public void save() {
        // Save selected Metadata into its own file
    }
}
