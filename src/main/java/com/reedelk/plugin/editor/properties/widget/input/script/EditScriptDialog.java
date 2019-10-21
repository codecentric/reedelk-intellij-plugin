package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.ScriptEditorDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EditScriptDialog extends DialogWrapper {

    private static final Dimension DEFAULT_SCRIPT_DIMENSION = new Dimension(600, 400);

    private ScriptEditorDefault editor;

    EditScriptDialog(@NotNull Module module,
                     @NotNull ScriptContextManager context, String initialValue) {
        super(module.getProject(), false);
        setTitle(Labels.DIALOG_TITLE_EDIT_SCRIPT);
        setResizable(true);

        editor = new ScriptEditorDefault(module.getProject(), context);
        editor.setValue(initialValue);
        editor.getComponent().setPreferredSize(DEFAULT_SCRIPT_DIMENSION);

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

    @Override
    public void dispose() {
        super.dispose();
        if (this.editor != null) {
            this.editor.dispose();
        }
    }

    public String getValue() {
        return editor.getValue();
    }
}
