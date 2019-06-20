package com.esb.plugin.configuration.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogAddConfiguration extends DialogWrapper {

    private final TypeObjectDescriptor typeObjectDescriptor;

    public DialogAddConfiguration(@NotNull Module module, TypeObjectDescriptor typeObjectDescriptor) {
        super(module.getProject(), false);
        this.typeObjectDescriptor = typeObjectDescriptor;
        this.setTitle(Labels.DIALOG_TITLE_ADD_CONFIGURATION);
        this.init();
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
        return new JPanel();
    }

    public void save() {

    }
}
