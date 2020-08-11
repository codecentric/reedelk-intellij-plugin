package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.editor.properties.commons.ChooseFileInputField;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.message.ReedelkBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;


public class OpenApiDialogSelectFile extends DialogWrapper {

    private final Project project;
    private final PropertyAccessorInMemory propertyAccessorInMemory = new PropertyAccessorInMemory();

    protected OpenApiDialogSelectFile(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(ReedelkBundle.message("openapi.importer.dialog.title"));
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
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
        JComboBox<String> modulesCombo = new ComboBox<>(comboBoxModel);

        // Build the panel
        DisposablePanel panel = new DisposablePanel(new GridBagLayout());
        FormBuilder.get().addLabel(message("openapi.importer.select.openapi.file.tile"), panel).addLastField(chooseFileInputField, panel);
        FormBuilder.get().addLabel(message("openapi.importer.select.module"), panel).addLastField(modulesCombo, panel);
        return panel;
    }

    String getOpenAPIFilePath() {
        return propertyAccessorInMemory.get();
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
