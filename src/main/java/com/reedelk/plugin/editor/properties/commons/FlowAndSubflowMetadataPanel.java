package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.reedelk.plugin.editor.properties.renderer.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class FlowAndSubflowMetadataPanel extends DisposablePanel implements Disposable {

    private final transient FlowSnapshot snapshot;

    public FlowAndSubflowMetadataPanel(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        initialize();
        setBorder(Borders.empty(10));
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    private void initialize() {
        DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());

        InputField<String> titleField = createTitleInputField();
        FormBuilder.get()
                .addLabel(message("flow.metadata.title"), propertiesPanel)
                .addLastField(titleField, propertiesPanel);

        InputField<String> descriptionField = createDescriptionInputField();
        FormBuilder.get()
                .addLabel(message("flow.metadata.description"), propertiesPanel)
                .addLastField(descriptionField, propertiesPanel);

        setLayout(new BorderLayout());
        add(propertiesPanel, NORTH);
        add(Box.createGlue(), CENTER);
    }

    private InputField<String> createTitleInputField() {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        return createStringInputField(graph.title(), message("flow.title.hint"), value -> {
            snapshot.getGraphOrThrowIfAbsent().setTitle((String) value);
            snapshot.onDataChange();
        });
    }

    private InputField<String> createDescriptionInputField() {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        return createStringInputField(graph.description(), message("flow.description.hint"), value -> {
            snapshot.getGraphOrThrowIfAbsent().setDescription((String) value);
            snapshot.onDataChange();
        });
    }

    private InputField<String> createStringInputField(String value, String hint, InputChangeListener changeListener) {
        InputField<String> inputField = new StringInputField(hint);
        inputField.setValue(value);
        inputField.addListener(changeListener);
        return inputField;
    }
}