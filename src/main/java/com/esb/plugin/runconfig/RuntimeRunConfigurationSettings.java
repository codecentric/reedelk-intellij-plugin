package com.esb.plugin.runconfig;

import com.esb.plugin.utils.ESBLabel;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.uiDesigner.core.GridConstraints.*;

public class RuntimeRunConfigurationSettings extends SettingsEditor<ESBRuntimeRunConfiguration> {

    private JPanel jPanel;
    private JTextField jdkTextField;
    private JTextField vmOptionsTextField;
    private JTextField runtimePortTextField;
    private TextFieldWithBrowseButton runtimeHomeField;

    public RuntimeRunConfigurationSettings(Project project) {
        buildRuntimeInputField(project);
    }


    private void buildRuntimeInputField(Project project) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        TextFieldWithBrowseButton inputWithBrowse = new TextFieldWithBrowseButton();
        inputWithBrowse.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, project) {
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                return chosenFile.getPath();
            }
        });

        JLabel label = new JBLabel(ESBLabel.RUNTIME_HOME.get());
        label.setLabelFor(inputWithBrowse);
        label.setVerticalAlignment(SwingConstants.TOP);


        jPanel.add(inputWithBrowse, CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS);

        this.runtimeHomeField = inputWithBrowse;
    }


    @Override
    protected void resetEditorFrom(@NotNull ESBRuntimeRunConfiguration s) {

    }

    @Override
    protected void applyEditorTo(@NotNull ESBRuntimeRunConfiguration s) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return jPanel;
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
