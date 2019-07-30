package com.esb.plugin.configuration.widget;

import com.esb.plugin.commons.Labels;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
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
