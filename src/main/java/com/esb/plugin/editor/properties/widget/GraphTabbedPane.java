package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import java.util.Collections;

public class GraphTabbedPane extends JBTabbedPane implements Disposable {

    private final Icon icon;
    private final String tabTitle;
    private final FlowSnapshot snapshot;

    public GraphTabbedPane(Icon icon, String tabTitle, FlowSnapshot snapshot) {
        super();
        this.icon = icon;
        this.tabTitle = tabTitle;
        this.snapshot = snapshot;
        initialize();
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    private void initialize() {
        FlowPropertiesPanel propertiesPanel = new FlowPropertiesPanel();

        InputField<String> titleField = createTitleInputField();
        FormBuilder.get()
                .addLabel(Labels.FLOW_GRAPH_TAB_TITLE, propertiesPanel)
                .addLastField(titleField, propertiesPanel);

        InputField<String> descriptionField = createDescriptionInputField();
        FormBuilder.get()
                .addLabel(Labels.FLOW_GRAPH_TAB_DESCRIPTION, propertiesPanel)
                .addLastField(descriptionField, propertiesPanel);

        JBPanel propertiesBoxPanel = ContainerFactory.createPropertiesBoxPanel(propertiesPanel);

        addTab(tabTitle, icon, propertiesBoxPanel, Labels.FLOW_GRAPH_TAB_TIP);
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

    class FlowPropertiesPanel extends DefaultPropertiesPanel {
        FlowPropertiesPanel() {
            super(null, Collections.emptyList(), null);
        }
    }
}
