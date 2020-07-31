package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.ChooseFileInputField;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class OpenApiDialogSelectFile extends DialogWrapper {

    private final Project project;
    private final PropertyAccessorInMemory propertyAccessorInMemory = new PropertyAccessorInMemory();

    protected OpenApiDialogSelectFile(@Nullable Project project) {
        super(project);
        this.project = project;
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JBLabel("Select OpenAPI"), BorderLayout.NORTH);

        // TODO: Extract strings

        String title = "Select Open API specification";
        String hintText = "Select Open API specification";

        ChooseFileInputField chooseFileInputField =
                new ChooseFileInputField(project,
                        title, hintText, StringUtils.EMPTY, propertyAccessorInMemory);
        panel.add(chooseFileInputField, BorderLayout.CENTER);
        return panel;
    }

    String getOpenAPIFilePath() {
        return propertyAccessorInMemory.get();
    }

    static class PropertyAccessorInMemory implements PropertyAccessor {

        private Object value = StringUtils.EMPTY;

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
