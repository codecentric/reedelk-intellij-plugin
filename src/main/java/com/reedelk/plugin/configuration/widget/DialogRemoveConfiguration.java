package com.reedelk.plugin.configuration.widget;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.commons.Labels;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogRemoveConfiguration extends DialogWrapper {

    DialogRemoveConfiguration(@NotNull Module module) {
        super(module.getProject(), false);
        setTitle(Labels.DIALOG_TITLE_DELETE_CONFIGURATION);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JLabel confirmLabel = new JLabel(Labels.DIALOG_MESSAGE_DELETE_CONFIRM);
        confirmLabel.setIcon(AllIcons.General.WarningDialog);
        return confirmLabel;
    }
}
