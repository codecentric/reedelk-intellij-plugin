package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public abstract class DialogWithMessage extends DialogWrapper {

    private static final int MAX_WIDTH = 450;

    private final DialogType dialogType;
    private final String message;

    protected DialogWithMessage(@Nullable Project project, String message, DialogType dialogType) {
        super(project, false);
        this.dialogType = dialogType;
        this.message = message;
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
        panel.add(ContainerFactory.pushTop(new JLabel(dialogType.icon)), WEST);

        JBLabel label = new JBLabel(message);
        label.setBorder(JBUI.Borders.empty(0, 20));
        panel.add(new DisposablePanelFixedWidth(label, MAX_WIDTH), CENTER);
        panel.setBorder(JBUI.Borders.empty(10, 5));
        return panel;
    }

    public enum DialogType {

        ERROR(AllIcons.General.ErrorDialog),
        INFO(AllIcons.General.InformationDialog);

        final Icon icon;

        DialogType(Icon icon) {
            this.icon = icon;
        }
    }
}
