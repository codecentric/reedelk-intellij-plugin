package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ErrorDialogImport extends DialogWrapper {

    protected ErrorDialogImport(@Nullable Project project) {
        super(project, false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return new JBLabel("Error importing ");
    }
}
