package de.codecentric.reedelk.plugin.action.openapi.dialog;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.plugin.commons.Images;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typeinteger.IntegerInputField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static de.codecentric.reedelk.plugin.commons.DefaultConstants.OpenApi;
import static de.codecentric.reedelk.plugin.editor.properties.commons.FileChooseInputFieldWithEraseBtn.PropertyAccessorInMemory;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isBlank;

public class DialogImport extends DialogWrapper {

    private static final int MAX_WIDTH = 450;

    private final Project project;
    private StringInputField targetDirectory;
    private StringInputField openApiURLField;
    private StringInputField basePath;
    private IntegerInputField openApiPort;
    private JComboBox<String> modulesCombo;
    private final PropertyAccessorInMemory propertyAccessorInMemory = new PropertyAccessorInMemory();

    public DialogImport(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(message("openapi.importer.dialog.import.title"));
        setSize(MAX_WIDTH, 0);
        setResizable(true);
        init();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (isBlank(getOpenApiFile()) && isBlank(getOpenApiURL())) {
            String message = message("openapi.importer.dialog.validation.error");
            return new ValidationInfo(message);
        }
        return super.doValidate();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // Text
        ImageIcon reedelkLogo = new ImageIcon(Images.Misc.ReedelkLogo);
        JBLabel reedelkLogoLabel = new JBLabel(reedelkLogo);
        reedelkLogoLabel.setBorder(JBUI.Borders.empty(0, 5));

        JBLabel label =
                new JBLabel(message("openapi.importer.dialog.import.help", String.valueOf(OpenApi.HTTP_PORT), OpenApi.BASE_PATH));
        label.setBorder(empty(5, 0));
        DisposablePanelFixedWidth fixedWidthPanel = new DisposablePanelFixedWidth(label, MAX_WIDTH);

        // File chooser
        FileChooseInputFieldWithEraseBtn chooseFileInputField =
                new FileChooseInputFieldWithEraseBtn(project,
                        message("openapi.importer.dialog.import.file.hint"),
                        message("openapi.importer.dialog.import.file.hint"),
                        EMPTY,
                        propertyAccessorInMemory);

        // Input Field
        this.openApiURLField = new StringInputField(message("openapi.importer.dialog.import.url.hint"));

        // Modules chooser
        Module[] modules = ModuleManager.getInstance(project).getModules();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        Arrays.stream(modules).forEach(module -> comboBoxModel.addElement(module.getName()));
        this.modulesCombo = new ComboBox<>(comboBoxModel);

        // Target directory
        this.targetDirectory = new StringInputField(OpenApi.TARGET_DIRECTORY);
        this.targetDirectory.setValue(OpenApi.TARGET_DIRECTORY);

        // API Port
        this.openApiPort = new IntegerInputField(String.valueOf(OpenApi.HTTP_PORT));

        // Base Path
        this.basePath = new StringInputField(OpenApi.BASE_PATH);

        // Build the center panel
        DisposablePanel centerPanel = new DisposablePanel(new GridBagLayout());

        FormBuilder.get()
                .addLabel(reedelkLogoLabel, centerPanel)
                .addLastField(fixedWidthPanel, centerPanel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.file"), centerPanel)
                .addLastField(ContainerFactory.pushLeft(chooseFileInputField), centerPanel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.url"), centerPanel)
                .addLastField(this.openApiURLField, centerPanel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.module"), centerPanel)
                .addLastField(this.modulesCombo, centerPanel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.directory"), centerPanel)
                .addLastField(this.targetDirectory, centerPanel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.port"), centerPanel)
                .addLastField(ContainerFactory.pushLeft(this.openApiPort), centerPanel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.basePath"), centerPanel)
                .addLastField(this.basePath, centerPanel);

        return ContainerFactory.pushTop(centerPanel);
    }

    public String getImportModule() {
        return (String) modulesCombo.getSelectedItem();
    }

    public String getOpenApiFile() {
        return propertyAccessorInMemory.get();
    }

    public String getTargetDirectory() {
        return (String) targetDirectory.getValue();
    }

    public String getOpenApiURL() {
        return (String) openApiURLField.getValue();
    }

    public String getBasePath() {
        return (String) basePath.getValue();
    }

    public Integer getOpenApiPort() {
        return (Integer) openApiPort.getValue();
    }
}
