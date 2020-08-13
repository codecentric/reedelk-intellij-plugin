package com.reedelk.plugin.action.openapi.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.action.openapi.Defaults;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.typeinteger.IntegerInputField;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;

public class DialogImport extends DialogWrapper {

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
        setResizable(true);
        init();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (isBlank(getOpenApiFile()) && isBlank(getOpenApiURL())) {
            String message = "OpenAPI resource not selected";
            return new ValidationInfo(message);
        }
        return super.doValidate();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // Text
        JBLabel label = new JBLabel();
        label.setText(message("openapi.importer.dialog.import.help"));
        label.setBorder(JBUI.Borders.empty(5, 0));

        // File chooser
        ChooseFileInputFieldWithEraseBtn chooseFileInputField =
                new ChooseFileInputFieldWithEraseBtn(project,
                        message("openapi.importer.dialog.import.file.hint"),
                        message("openapi.importer.dialog.import.file.hint"),
                        EMPTY,
                        propertyAccessorInMemory);

        // Input Field
        this.openApiURLField = new StringInputField("https://example.com/api/openapi.yaml");

        // Modules chooser
        Module[] modules = ModuleManager.getInstance(project).getModules();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        Arrays.stream(modules).forEach(module -> comboBoxModel.addElement(module.getName()));
        this.modulesCombo = new ComboBox<>(comboBoxModel);

        // Target directory
        this.targetDirectory = new StringInputField(Defaults.TARGET_DIRECTORY);
        this.targetDirectory.setValue(Defaults.TARGET_DIRECTORY);

        // API Port
        this.openApiPort = new IntegerInputField(String.valueOf(Defaults.HTTP_PORT));

        // Base Path
        this.basePath = new StringInputField(Defaults.BASE_PATH);

        // Build the center panel
        DisposablePanel centerPanel = new DisposablePanel(new GridBagLayout());

        FormBuilder.get()
                .addLastField(label, centerPanel);
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
                .addLastField(this.openApiPort, centerPanel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.basePath"), centerPanel)
                .addLastField(this.basePath, centerPanel);
        return centerPanel;
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

    static class PropertyAccessorInMemory implements PropertyAccessor {

        private Object value = EMPTY;

        @Override
        public FlowSnapshot getSnapshot() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> void set(T object) {
            this.value = object;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get() {
            return (T) this.value;
        }

        @Override
        public String getProperty() {
            throw new UnsupportedOperationException();
        }
    }
}
