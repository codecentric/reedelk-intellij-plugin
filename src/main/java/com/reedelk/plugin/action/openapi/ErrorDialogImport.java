package com.reedelk.plugin.action.openapi;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ErrorDialogImport extends DialogWrapper {

    protected ErrorDialogImport(@Nullable Project project) {
        super(project, false);
        setTitle("Import OpenAPI Error");
        setResizable(false);
        init();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        DisposablePanel panel = new DisposablePanel(new BorderLayout());
        panel.add(new JLabel(AllIcons.General.ErrorDialog), BorderLayout.WEST);

        JBLabel label = new JBLabel("Error importing OpenAPI file");
        label.setBorder(JBUI.Borders.empty(0, 20));
        panel.add(label, BorderLayout.CENTER);
        panel.setBorder(JBUI.Borders.empty(10, 5));
        return panel;
    }
}
