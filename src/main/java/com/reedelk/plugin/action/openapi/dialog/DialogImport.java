package com.reedelk.plugin.action.openapi.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.ChooseFileInputField;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.commons.StringInputField;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
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
    private JComboBox<String> modulesCombo;
    private final PropertyAccessorInMemory propertyAccessorInMemory = new PropertyAccessorInMemory();

    public DialogImport(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(message("openapi.importer.dialog.import.title"));
        setResizable(false);
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
        ChooseFileInputField chooseFileInputField =
                new ChooseFileInputField(project,
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
        this.targetDirectory = new StringInputField("myApi");
        this.targetDirectory.setValue("myApi");

        // Build the panel
        DisposablePanel panel = new DisposablePanel(new GridBagLayout());

        FormBuilder.get()
                .addLastField(label, panel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.file"), panel)
                .addLastField(chooseFileInputField, panel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.url"), panel)
                .addLastField(this.openApiURLField, panel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.module"), panel)
                .addLastField(this.modulesCombo, panel);
        FormBuilder.get()
                .addLabel(message("openapi.importer.dialog.import.directory"), panel)
                .addLastField(this.targetDirectory, panel);
        return panel;
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
