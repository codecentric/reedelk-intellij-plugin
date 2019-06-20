package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogRemoveConfiguration extends DialogWrapper {

    private ConfigMetadata selectedMetadata;

    public DialogRemoveConfiguration(@Nullable Project project) {
        super(project, false);
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

    public void onSelect(ConfigMetadata selectedMetadata) {
        this.selectedMetadata = selectedMetadata;
    }

    public void delete() {

    }
}
