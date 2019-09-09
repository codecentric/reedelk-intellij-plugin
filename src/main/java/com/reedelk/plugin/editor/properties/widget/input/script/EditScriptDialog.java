package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.JavascriptEditor;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.JavascriptEditorDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;

public class EditScriptDialog extends DialogWrapper {

    private JavascriptEditor editor;

    // An editor without extra context variables (just the default ones)
    // - such as the one used in the Router component or in the Logger component -
    public EditScriptDialog(@NotNull Module module, @NotNull String initialValue) {
        this(module, initialValue, new ScriptContextManager(module, new EmptyPanelContext(), Collections.emptyList()));
    }

    EditScriptDialog(@NotNull Module module,
                     @NotNull String initialValue,
                     @NotNull ScriptContextManager context) {
        super(module.getProject(), false);
        setTitle(Labels.DIALOG_TITLE_EDIT_SCRIPT);
        setResizable(true);

        editor = new JavascriptEditorDefault(module.getProject(), context);
        editor.setValue(initialValue);

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
        return editor.getComponent();
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
