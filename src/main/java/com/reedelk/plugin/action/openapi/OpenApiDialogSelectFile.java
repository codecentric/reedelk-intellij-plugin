package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
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


public class OpenApiDialogSelectFile extends DialogWrapper {

    private final Project project;
    private StringInputField targetDirectory;
    private JComboBox<String> modulesCombo;
    private final PropertyAccessorInMemory propertyAccessorInMemory = new PropertyAccessorInMemory();

    protected OpenApiDialogSelectFile(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(message("openapi.importer.dialog.title"));
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // Text
        JBLabel label = new JBLabel();
        label.setText(message("openapi.importer.help"));
        label.setBorder(JBUI.Borders.empty(5, 0));

        // File chooser
        ChooseFileInputField chooseFileInputField =
                new ChooseFileInputField(project,
                        message("openapi.importer.select.openapi.file.choose.tile"),
                        message("openapi.importer.select.openapi.file.choose.hint"),
                        EMPTY,
                        propertyAccessorInMemory);

        // Modules chooser
        Module[] modules = ModuleManager.getInstance(project).getModules();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        Arrays.stream(modules).forEach(module -> comboBoxModel.addElement(module.getName()));
        this.modulesCombo = new ComboBox<>(comboBoxModel);

        // Target directory
        targetDirectory = new StringInputField("myApi");
        targetDirectory.setValue("myApi");

        // Build the panel
        DisposablePanel panel = new DisposablePanel(new GridBagLayout());
        FormBuilder.get().addLastField(label, panel);
        FormBuilder.get().addLabel(message("openapi.importer.select.openapi.file.tile"), panel).addLastField(chooseFileInputField, panel);
        FormBuilder.get().addLabel(message("openapi.importer.select.module"), panel).addLastField(modulesCombo, panel);
        FormBuilder.get().addLabel(message("openapi.importer.target.directory"), panel).addLastField(targetDirectory, panel);
        return panel;
    }

    String getImportModule() {
        return (String) modulesCombo.getSelectedItem();
    }

    String getOpenAPIFilePath() {
        return propertyAccessorInMemory.get();
    }

    public String getTargetDirectory() {
        return (String) targetDirectory.getValue();
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
