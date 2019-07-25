package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.Disposable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class FlowMetadataPanel extends DisposablePanel implements Disposable {

    private final FlowSnapshot snapshot;

    public FlowMetadataPanel(FlowSnapshot snapshot) {
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
        FlowGraph graph = snapshot.getGraph();
        return createStringInputField(graph.title(), value -> {
            graph.setTitle(value);
            snapshot.onDataChange();
        });
    }

    private InputField<String> createDescriptionInputField() {
        FlowGraph graph = snapshot.getGraph();
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
