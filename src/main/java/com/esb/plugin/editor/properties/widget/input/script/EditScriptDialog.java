package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Labels;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EditScriptDialog extends DialogWrapper {

    private JavascriptEditor editor;

    public EditScriptDialog(@NotNull Module module, @NotNull String initialValue) {
        super(module.getProject(), false);
        setTitle(Labels.DIALOG_TITLE_EDIT_SCRIPT);
        setResizable(true);

        editor = new JavascriptEditor(module.getProject(), initialValue);

        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, Labels.DIALOG_BTN_SAVE_SCRIPT);
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return editor;
    }

    public String getText() {
        return editor.getText();
    }
}
