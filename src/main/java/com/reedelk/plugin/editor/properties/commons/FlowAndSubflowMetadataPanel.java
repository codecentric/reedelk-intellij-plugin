package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.commons.Labels.Hint;
import com.reedelk.plugin.editor.properties.renderer.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class FlowAndSubflowMetadataPanel extends DisposablePanel implements Disposable {

    private final FlowSnapshot snapshot;

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
        return createStringInputField(graph.title(), Hint.FLOW_SUBFLOW_TITLE, value -> {
            graph.setTitle((String) value);
            snapshot.onDataChange();
        });
    }

    private InputField<String> createDescriptionInputField() {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        return createStringInputField(graph.description(), Hint.FLOW_SUBFLOW_DESCRIPTION, value -> {
            graph.setDescription((String) value);
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
