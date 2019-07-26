package com.esb.plugin.runconfig.runtime;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.uiDesigner.core.GridConstraints.*;

public class RuntimeRunConfigurationSettings extends SettingsEditor<RuntimeRunConfiguration> {

    private JPanel jPanel;
    private JTextField vmOptionsTextField;
    private JTextField runtimePortTextField;
    private JTextField runtimeAddressTextField;
    private TextFieldWithBrowseButton runtimeHomeField;

    public RuntimeRunConfigurationSettings(Project project) {
        buildRuntimeInputField(project);
    }

    @Override
    protected void resetEditorFrom(@NotNull RuntimeRunConfiguration runtimeRunConfiguration) {
        vmOptionsTextField.setText(runtimeRunConfiguration.getVmOptions());
        runtimePortTextField.setText(runtimeRunConfiguration.getRuntimePort());
        runtimeHomeField.setText(runtimeRunConfiguration.getRuntimeHomeDirectory());
        runtimeAddressTextField.setText(runtimeRunConfiguration.getRuntimeBindAddress());
    }

    @Override
    protected void applyEditorTo(@NotNull RuntimeRunConfiguration configuration) throws ConfigurationException {
        configuration.setRuntimeHomeDirectory(runtimeHomeField.getText());
        configuration.setVmOptions(vmOptionsTextField.getText());
        configuration.setRuntimePort(runtimePortTextField.getText());
        configuration.setRuntimeBindAddress(runtimeAddressTextField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
    }

    private void buildRuntimeInputField(Project project) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        runtimeHomeField = new TextFieldWithBrowseButton();
        runtimeHomeField.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, project) {
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                return chosenFile.getPath();
            }
        });
        jPanel.add(runtimeHomeField, CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS);
    }

    private static final GridConstraints CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS =
            new GridConstraints(0, 3, 1, 1,
                    ANCHOR_WEST,
                    FILL_HORIZONTAL,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    new Dimension(-1, -1),
                    new Dimension(-1, -1),
                    new Dimension(-1, -1));

}
