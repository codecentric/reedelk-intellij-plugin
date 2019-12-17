package com.reedelk.plugin.builder;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.UI;
import com.reedelk.plugin.commons.RuntimeComboManager;
import com.reedelk.plugin.validator.RuntimeHomeValidator;
import com.reedelk.plugin.validator.Validator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.util.text.StringUtil.commonPrefixLength;
import static com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces;
import static com.intellij.uiDesigner.core.GridConstraints.*;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Collections.singletonList;

public class ConfigureRuntimeStep extends ModuleWizardStep implements ItemListener, Disposable {

    private ModuleBuilder moduleBuilder;

    private JPanel jPanel;
    private JLabel runtimeName;
    private JPanel addRuntimePanel;
    private JPanel chooseRuntimePanel;
    private JComboBox<String> runtimeCombo;
    private JTextField runtimeConfigNameTextField;
    private TextFieldWithBrowseButton runtimeHomeDirectoryBrowse;
    private JBLoadingPanel loadingPanel = new JBLoadingPanel(new BorderLayout(), this, 100);

    private RuntimeComboManager runtimeComboManager;

    private boolean isNewProject;

    @Override
    public void _init() {
        super._init();
        if (moduleBuilder.isDownloadDistribution()) {
            loadingPanel.getContentPanel().removeAll();
            loadingPanel.startLoading();
            loadingPanel.setLoadingText("Downloading Reedelk ESB runtime ...");
            getApplication().executeOnPooledThread(() -> {
                // Download the runtime
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        loadingPanel.getContentPanel().add(jPanel);
                        loadingPanel.stopLoading();
                        loadingPanel.revalidate();
                        loadingPanel.repaint();
                    });
                }
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                loadingPanel.getContentPanel().add(jPanel);
                jPanel.revalidate();
            });
        }
    }

    public ConfigureRuntimeStep(WizardContext wizardContext, ModuleBuilder builder) {
        isNewProject = wizardContext.isCreatingNewProject();
        if (isNewProject) {
            chooseRuntimePanel.setVisible(false);
        } else {
            addRuntimePanel.setVisible(false);
        }
        moduleBuilder = builder;
        runtimeComboManager = new RuntimeComboManager(
                runtimeCombo,
                wizardContext.getProject(),
                singletonList(message("runtimeBuilder.add.new.config")),
                this);
        runtimeConfigNameChanged(runtimeComboManager.getRuntimeConfigName());
        createInputWithBrowse(wizardContext, moduleBuilder);
    }

    @Override
    public JComponent getComponent() {
        return loadingPanel;
    }

    @Override
    public void updateDataModel() {
        if (isNewProject) {
            moduleBuilder.setRuntimeConfigName(runtimeConfigNameTextField.getText());
            moduleBuilder.setRuntimeHomeDirectory(runtimeHomeDirectoryBrowse.getText());
            moduleBuilder.createRuntimeConfig(true);
        } else {
            if (message("runtimeBuilder.add.new.config").equals(runtimeComboManager.getRuntimeConfigName())) {
                moduleBuilder.setRuntimeConfigName(runtimeConfigNameTextField.getText());
                moduleBuilder.setRuntimeHomeDirectory(runtimeHomeDirectoryBrowse.getText());
                moduleBuilder.createRuntimeConfig(true);
            } else {
                moduleBuilder.setRuntimeConfigName(runtimeComboManager.getRuntimeConfigName());
            }
        }
    }

    @Override
    public boolean validate() throws ConfigurationException {
        List<String> errors = new ArrayList<>();

        // Validating new project
        if (isNewProject) {
            if (isEmptyOrSpaces(runtimeConfigNameTextField.getText())) {
                errors.add("Runtime Name can not be empty");
            }
            if (isEmptyOrSpaces(runtimeHomeDirectoryBrowse.getText())) {
                errors.add("Runtime Path can not be empty");
            }

            Validator validator = new RuntimeHomeValidator(runtimeHomeDirectoryBrowse.getText());
            validator.validate(errors);

            // Validating adding new module
        } else {
            if (isEmptyOrSpaces(runtimeComboManager.getRuntimeConfigName())) {
                errors.add("A runtime must be selected");
            }
        }

        return errors.isEmpty();
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            String newConfigName = (String) event.getItem();
            runtimeConfigNameChanged(newConfigName);
        }
    }

    private void runtimeConfigNameChanged(String newRuntimeConfigName) {
        if (message("runtimeBuilder.add.new.config").equals(newRuntimeConfigName)) {
            addRuntimePanel.setVisible(true);
        } else {
            addRuntimePanel.setVisible(false);
        }
    }

    private void createInputWithBrowse(WizardContext context, ModuleBuilder moduleBuilder) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        runtimeHomeDirectoryBrowse = new TextFieldWithBrowseButton();
        runtimeHomeDirectoryBrowse.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, context.getProject()) {
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                String contentEntryPath = moduleBuilder.getContentEntryPath();
                String path = chosenFile.getPath();
                return contentEntryPath == null ? path : path.substring(commonPrefixLength(contentEntryPath, path));
            }
        });
        JPanel runtimeChooserPanel = UI.PanelFactory.panel(runtimeHomeDirectoryBrowse).
                withComment(message("runtimeBuilder.runtime.home.directory"))
                .createPanel();
        addRuntimePanel.add(runtimeChooserPanel, CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS);
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

    @Override
    public void dispose() {

    }
}
