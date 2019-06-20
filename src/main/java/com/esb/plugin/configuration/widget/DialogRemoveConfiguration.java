package com.esb.plugin.configuration.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogRemoveConfiguration extends DialogWrapper {

    private final Module module;
    private final ConfigMetadata selectedConfig;

    public DialogRemoveConfiguration(@NotNull Module module, @NotNull ConfigMetadata selectedConfig) {
        super(module.getProject(), false);
        this.module = module;
        this.selectedConfig = selectedConfig;

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

    public void delete() {
        ConfigService.getInstance(module)
                .removeConfig(selectedConfig);
    }
}
