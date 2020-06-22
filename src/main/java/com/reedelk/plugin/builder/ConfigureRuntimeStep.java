package com.reedelk.plugin.builder;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Disposer;
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
    private JLabel downloadedZipFile;
    private JLabel downloadedZipFileIcon;
    private TextFieldWithBrowseButton runtimeHomeDirectoryBrowse;
    private JBLoadingPanel loadingPanel;

    private RuntimeComboManager runtimeComboManager;

    private boolean isNewProject;
    private JPanel runtimeChooserPanel;
    private DownloadTask downloadTask;

    @Override
    public void onStepLeaving() {
        super.onStepLeaving();
        if (downloadTask != null) downloadTask.cancel();
        downloadTask = null;
    }

    @Override
    public void _init() {
        super._init();
        loadingPanel = new JBLoadingPanel(new BorderLayout(), this, 100);
        downloadedZipFile.setVisible(false);

        loadingPanel.getContentPanel().removeAll();
        if (shouldDownloadDistribution()) {
            loadingPanel.startLoading();
            loadingPanel.setLoadingText(message("runtimeBuilder.downloading.distribution"));

            downloadTask = new DownloadTask();
            getApplication().executeOnPooledThread(downloadTask);


        } else if (isDownloadedAlready()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                setRuntimeHomeVisible(false);
                downloadedZipFileIcon.setVisible(true);
                downloadedZipFile.setVisible(true);
                downloadedZipFile.setText(moduleBuilder.getTmpDownloadDistributionPath().get().toString());
                loadingPanel.getContentPanel().add(jPanel);
                loadingPanel.revalidate();
                loadingPanel.repaint();
            });
        } else {
            ApplicationManager.getApplication().invokeLater(() -> {
                setRuntimeHomeVisible(true);
                downloadedZipFileIcon.setVisible(false);
                downloadedZipFile.setVisible(false);
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
                    if (downloadTask != null) {
                        errors.add("Please wait until the Reedelk runtime distribution has been downloaded.");
                    } else {
                        errors.add("Could not download, please try again.");
                    }
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
    public void disposeUIResources() {
        super.disposeUIResources();
        Disposer.dispose(this);
    }

    @Override
    public void dispose() {
        // Nothing to do (already disposed in the method above)
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

    private boolean isDownloadedAlready() {
        return moduleBuilder.isDownloadDistribution() &&
                moduleBuilder.getTmpDownloadDistributionPath().isPresent();
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

    class DownloadTask implements Runnable {

        private RuntimeDistributionHelper distributionHelper = new RuntimeDistributionHelper();
        private boolean cancelled;

        @Override
        public void run() {
            try {
                final int startStep = wizardContext.getWizard().getCurrentStep();

                // Download and Unzip the runtime
                Path downloadDistributionPath =
                        distributionHelper.downloadAndUnzip(message -> loadingPanel.setLoadingText(message));

                // If it has not been cancelled, and we are on the same step, then we can move on.
                if (!cancelled && startStep == wizardContext.getWizard().getCurrentStep()) {

                    // The configuration name equals the name of the downloaded runtime distribution
                    runtimeConfigNameTextField.setText(downloadDistributionPath.getFileName().toString());
                    moduleBuilder.setTmpDownloadDistributionPath(downloadDistributionPath);

                    // We stop the loading spinning wheel and move on to the next step.
                    loadingPanel.stopLoading();
                    wizardContext.getWizard().clickDefaultButton();
                }

            } catch (Exception exception) {

                String errorMessage = message("runtimeBuilder.downloading.distribution.error.message", exception.getMessage());
                String errorTitle = message("runtimeBuilder.downloading.distribution.error.title");
                DownloadErrorPanel errorPanel = new DownloadErrorPanel();
                errorPanel.setTitle(errorTitle);
                errorPanel.setMessage(errorMessage);
                loadingPanel.getContentPanel().add(errorPanel.content());
                loadingPanel.stopLoading();
                loadingPanel.setLoadingText("");
                loadingPanel.revalidate();
                loadingPanel.repaint();

                downloadTask = null;
            }
        }

        public void cancel() {
            if (distributionHelper != null) {
                distributionHelper.cancel();
            }
            this.cancelled = true;
        }
    }
}
