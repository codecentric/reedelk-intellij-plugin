package com.esb.plugin.runconfig;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RuntimeRunConfigurationSettings extends SettingsEditor<RunConfiguration> {

    private JPanel jPanel;
    private JTextField runtimeHomeTextField;
    private JTextField vmOptionsTextField;
    private JTextField jdkTextField;
    private JTextField runtimePortTextField;

    @Override
    protected void resetEditorFrom(@NotNull RunConfiguration configuration) {

    }

    @Override
    protected void applyEditorTo(@NotNull RunConfiguration configuration) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
    }
}
