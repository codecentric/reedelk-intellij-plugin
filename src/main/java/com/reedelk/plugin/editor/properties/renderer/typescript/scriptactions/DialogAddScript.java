package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class DialogAddScript extends DialogWrapper {

    private String scriptFileName;

    DialogAddScript(@Nullable Project project) {
        super(project, false);
        setTitle(Labels.ADD_SCRIPT);
        setResizable(false);
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
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new GridBagLayout());

        InputField<String> field = new StringInputField(StringUtils.EMPTY);
        field.addListener(value -> scriptFileName = (String) value);
        FormBuilder.get()
                .addLabel("Script file name", panel)
                .addLastField(field, panel);

        panel.setPreferredSize(new Dimension(350, 0));
        return panel;
    }

    String getScriptFileName() {
        return scriptFileName;
    }
}
