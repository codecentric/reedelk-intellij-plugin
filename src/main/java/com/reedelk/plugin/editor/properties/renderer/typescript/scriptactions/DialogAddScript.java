package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class DialogAddScript extends DialogWrapper {

    private String scriptFileNameWithPathToAdd;

    DialogAddScript(@Nullable Project project) {
        super(project, false);
        setTitle(message("script.dialog.add.title"));
        setResizable(false);
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, message("script.dialog.add.btn.add"));
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new GridBagLayout());

        InputField<String> field = new StringInputField(StringUtils.EMPTY);
        field.addListener(value -> scriptFileNameWithPathToAdd = (String) value);
        FormBuilder.get()
                .addLabel(message("script.dialog.add.label.file.name"), panel)
                .addLastField(field, panel);

        panel.setPreferredSize(new Dimension(350, 0));
        return panel;
    }

    String getScriptFileNameWithPathToAdd() {
        return scriptFileNameWithPathToAdd;
    }
}
