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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.util.text.StringUtil.commonPrefixLength;
import static com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces;
import static com.intellij.uiDesigner.core.GridConstraints.*;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.lang.String.join;
import static java.util.Collections.singletonList;
import static javax.swing.SwingUtilities.invokeLater;

public class ConfigureRuntimeStep extends ModuleWizardStep implements ItemListener, Disposable {

    private final WizardContext wizardContext;
    private ModuleBuilder moduleBuilder;

    private JPanel jPanel;
    private JLabel runtimeName;
    private JLabel runtimeHome;
    private JPanel addRuntimePanel;
    private JPanel chooseRuntimePanel;
    private JComboBox<String> runtimeCombo;
    private JTextField runtimeConfigNameTextField;
    private TextFieldWithBrowseButton runtimeHomeDirectoryBrowse;
    private JBLoadingPanel loadingPanel = new JBLoadingPanel(new BorderLayout(), this, 100);

    private RuntimeComboManager runtimeComboManager;

    private boolean isNewProject;
    private JPanel runtimeChooserPanel;

    @Override
    public void _init() {
        super._init();
        loadingPanel.getContentPanel().removeAll();
        if (shouldDownloadDistribution()) {
            loadingPanel.startLoading();
            loadingPanel.setLoadingText(message("runtimeBuilder.downloading.distribution"));
            getApplication().executeOnPooledThread(() -> {

                invokeLater(() -> wizardContext.getWizard().updateButtons(false, false, false));
                try {
                    // Download and Unzip the runtime
                    Path downloadDistributionPath = RuntimeDistributionHelper.downloadAndUnzip();

                    // The configuration name equals the name of the downloaded runtime distribution
                    runtimeConfigNameTextField.setText(downloadDistributionPath.getFileName().toString());
                    moduleBuilder.setTmpDownloadDistributionPath(downloadDistributionPath);

                    invokeLater(() -> {
                        wizardContext.getWizard().updateButtons(false, true, false);

                        // We stop the loading spinning wheel and move on to the next step.
                        loadingPanel.stopLoading();
                        wizardContext.getWizard().clickDefaultButton();
                    });

                } catch (Exception exception) {
                    invokeLater(() -> {
                        String errorMessage = message("runtimeBuilder.downloading.distribution.error.message", exception.getMessage());
                        String errorTitle = message("runtimeBuilder.downloading.distribution.error.title");
                        DownloadErrorPanel errorPanel = new DownloadErrorPanel();
                        errorPanel.setTitle(errorTitle);
                        errorPanel.setMessage(errorMessage);

                        loadingPanel.getContentPanel().add(errorPanel.content());
                        loadingPanel.stopLoading();
                        loadingPanel.revalidate();
                        loadingPanel.repaint();
                        wizardContext.getWizard().updateButtons(false, false, false);
                    });
                }
            });
        } else {
            invokeLater(() -> {
                setRuntimeHomeVisible(true);
                loadingPanel.getContentPanel().add(jPanel);
                loadingPanel.revalidate();
                loadingPanel.repaint();
            });
        }
    }

    public ConfigureRuntimeStep(WizardContext wizardContext, ModuleBuilder builder) {
        this.wizardContext = wizardContext;
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
        if (isNewProject) {
            // Validating new project
            if (isEmptyOrSpaces(runtimeConfigNameTextField.getText())) {
                errors.add(message("runtimeBuilder.runtime.config.name.validator.empty"));
            }
            if (!moduleBuilder.isDownloadDistribution()) {
                Validator validator = new RuntimeHomeValidator(runtimeHomeDirectoryBrowse.getText());
                validator.validate(errors);
            }
            if (moduleBuilder.isDownloadDistribution()) {
                if (!moduleBuilder.getTmpDownloadDistributionPath().isPresent()) {
                    errors.add("Could not download, please try again.");
                }
            }
        } else {
            // Validating adding new module
            if (isEmptyOrSpaces(runtimeComboManager.getRuntimeConfigName())) {
                errors.add(message("runtimeBuilder.runtime.config.combo.validator.not.selected"));
            }
        }
        if (!errors.isEmpty()) {
            throw new ConfigurationException(join(",", errors));
        }
        return true;
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            String newConfigName = (String) event.getItem();
            runtimeConfigNameChanged(newConfigName);
        }
    }

    @Override
    public void dispose() {
        // Nothing to do
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
        this.runtimeChooserPanel = UI.PanelFactory.panel(runtimeHomeDirectoryBrowse).
                withComment(message("runtimeBuilder.runtime.home.directory"))
                .createPanel();
        addRuntimePanel.add(runtimeChooserPanel, CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS);
    }

    private void setRuntimeHomeVisible(boolean visible) {
        runtimeChooserPanel.setVisible(visible);
        runtimeHome.setVisible(visible);
    }

    private boolean shouldDownloadDistribution() {
        return moduleBuilder.isDownloadDistribution() &&
                !moduleBuilder.getTmpDownloadDistributionPath().isPresent();
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
