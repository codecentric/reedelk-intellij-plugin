package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogConfirmAction extends DialogWrapper {

    private final String confirmActionMessage;

    public DialogConfirmAction(@NotNull Module module, String confirmDialogTitle, String confirmActionMessage) {
        super(module.getProject(), false);
        this.confirmActionMessage = confirmActionMessage;
        setTitle(confirmDialogTitle);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JLabel confirmLabel = new JLabel(confirmActionMessage);
        confirmLabel.setIcon(AllIcons.General.WarningDialog);
        return confirmLabel;
    }
}
