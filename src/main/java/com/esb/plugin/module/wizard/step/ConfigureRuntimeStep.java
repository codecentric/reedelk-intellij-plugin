package com.esb.plugin.module.wizard.step;

import com.esb.plugin.module.ESBModuleBuilder;
import com.esb.plugin.ui.RuntimeComboManager;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.UI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.uiDesigner.core.GridConstraints.*;

public class ConfigureRuntimeStep extends ModuleWizardStep implements Disposable {

    private ESBModuleBuilder moduleBuilder;
    
    private JPanel jPanel;
    private JLabel runtimeName;
    private JPanel addRuntimePanel;
    private JPanel chooseRuntimePanel;
    private JComboBox<String> runtimeCombo;
    private JTextField runtimeConfigNameTextField;
    private TextFieldWithBrowseButton inputWithBrowse;

    private RuntimeComboManager runtimeComboManager;

    private boolean isNewProject;

    public ConfigureRuntimeStep(WizardContext wizardContext, ESBModuleBuilder builder, Project project) {
        isNewProject = wizardContext.isCreatingNewProject();
        if (isNewProject) {
            chooseRuntimePanel.setVisible(false);
        } else {
            addRuntimePanel.setVisible(false);
        }

        this.moduleBuilder = builder;
        runtimeComboManager = new RuntimeComboManager(runtimeCombo, project);
        createInputWithBrowse(wizardContext, moduleBuilder);
    }

    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void updateDataModel() {

        moduleBuilder.isNewProject(isNewProject);
        if (isNewProject) {
            moduleBuilder.setRuntimeConfigName(runtimeConfigNameTextField.getText());
            moduleBuilder.setRuntimeHomeDirectory(inputWithBrowse.getText());
        } else {
            moduleBuilder.setRuntimeConfigName(runtimeComboManager.getRuntimeConfigName());
        }

    }

    @Override
    public void dispose() {

    }

    private void createInputWithBrowse(WizardContext context, ESBModuleBuilder moduleBuilder) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        inputWithBrowse = new TextFieldWithBrowseButton();
        inputWithBrowse.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, context.getProject()) {
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                String contentEntryPath = moduleBuilder.getContentEntryPath();
                String path = chosenFile.getPath();
                return contentEntryPath == null ? path : path.substring(StringUtil.commonPrefixLength(contentEntryPath, path));
            }
        });

        JPanel runtimeChooserPanel = UI.PanelFactory.panel(inputWithBrowse).
                withComment("Select ESB runtime home directory this project will be using. " +
                        "The runtime will be used to provide a default configuration for this project.")
                .createPanel();


        addRuntimePanel.add(runtimeChooserPanel, CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS);
    }

    @Override
    public boolean validate() throws ConfigurationException {
        List<String> errors = new ArrayList<>();
        if (StringUtil.isEmptyOrSpaces(runtimeConfigNameTextField.getText())) errors.add("Runtime Name");
        if (StringUtil.isEmptyOrSpaces(inputWithBrowse.getText())) errors.add("Runtime Path");
        return errors.isEmpty();
    }

    private static final GridConstraints CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS =
            new GridConstraints(1, 1, 1, 1,
                    ANCHOR_WEST,
                    FILL_HORIZONTAL,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    new Dimension(-1, -1),
                    new Dimension(-1, -1),
                    new Dimension(-1, -1));
}
