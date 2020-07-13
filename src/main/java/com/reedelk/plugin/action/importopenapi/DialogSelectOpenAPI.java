package com.reedelk.plugin.action.importopenapi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.ChooseFileInputField;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class DialogSelectOpenAPI extends DialogWrapper {

    private final Project project;
    private final PropertyAccessorInMemory propertyAccessorInMemory = new PropertyAccessorInMemory();

    protected DialogSelectOpenAPI(@Nullable Project project) {
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
}
