package com.reedelk.plugin.editor.properties.widget;

import com.intellij.openapi.Disposable;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.InputField;
import com.reedelk.plugin.editor.properties.widget.input.StringInputField;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class FlowAndSubflowMetadataPanel extends DisposablePanel implements Disposable {

    private final FlowSnapshot snapshot;

    public FlowAndSubflowMetadataPanel(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        initialize();
        setBorder(JBUI.Borders.empty(10));
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    private void initialize() {
        DisposablePanel propertiesPanel = new PropertiesPanelHolder(null, Collections.emptyList(), null);

        InputField<String> titleField = createTitleInputField();
        FormBuilder.get()
                .addLabel(Labels.FLOW_GRAPH_TAB_TITLE, propertiesPanel)
                .addLastField(titleField, propertiesPanel);

        InputField<String> descriptionField = createDescriptionInputField();
        FormBuilder.get()
                .addLabel(Labels.FLOW_GRAPH_TAB_DESCRIPTION, propertiesPanel)
                .addLastField(descriptionField, propertiesPanel);

        setLayout(new BorderLayout());
        add(propertiesPanel, NORTH);
        add(Box.createGlue(), CENTER);
    }

    private InputField<String> createTitleInputField() {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        return createStringInputField(graph.title(), value -> {
            graph.setTitle(value);
            snapshot.onDataChange();
        });
    }

    private InputField<String> createDescriptionInputField() {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        return createStringInputField(graph.description(), value -> {
            graph.setDescription(value);
            snapshot.onDataChange();
        });
    }

    private InputField<String> createStringInputField(String value, InputChangeListener<String> changeListener) {
        InputField<String> inputField = new StringInputField();
        inputField.setValue(value);
        inputField.addListener(changeListener);
        return inputField;
    }
}
