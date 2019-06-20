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

public class DialogAddConfiguration extends DialogWrapper {

    private final TypeObjectDescriptor objectDescriptor;
    private final ConfigMetadata newConfigMetadata;
    private final Module module;

    public DialogAddConfiguration(@NotNull Module module, TypeObjectDescriptor objectDescriptor, ConfigMetadata newConfig) {
        super(module.getProject(), false);
        this.objectDescriptor = objectDescriptor;
        this.newConfigMetadata = newConfig;
        this.module = module;

        setTitle(Labels.DIALOG_TITLE_ADD_CONFIGURATION);
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, Labels.DIALOG_BTN_ADD_CONFIGURATION);
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return new ConfigPropertiesPanel(module, newConfigMetadata, objectDescriptor, true);
    }

    public void add() {
        ConfigService.getInstance(module).addConfig(newConfigMetadata);
    }
}
