package com.esb.plugin.runconfig.module;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ESBModuleRunConfigurationSettings extends SettingsEditor<ESBModuleRunConfiguration> {

    // Make Module Selectable
    // Make Runtime Selectable
    private JPanel jPanel;

    @Override
    protected void resetEditorFrom(@NotNull ESBModuleRunConfiguration configuration) {

    }

    @Override
    protected void applyEditorTo(@NotNull ESBModuleRunConfiguration configuration) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
    }
}
