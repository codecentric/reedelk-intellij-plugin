package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Labels;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogEditConfiguration extends DialogWrapper {

    private final JComponent centerPanel;

    public DialogEditConfiguration(@Nullable Project project, JComponent centerPanel) {
        super(project, false);
        this.centerPanel = centerPanel;
        this.setTitle(Labels.DIALOG_TITLE_EDIT_CONFIGURATION);
        this.init();
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
        return centerPanel;
    }
}
