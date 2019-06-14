package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;

public class GraphTabbedPane extends JBTabbedPane {

    private final FlowSnapshot snapshot;

    public GraphTabbedPane(FlowSnapshot snapshot) {
        super();
        this.snapshot = snapshot;
        initialize();
    }

    private void initialize() {
        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();

        InputField<String> titleField = createTitleInputField();
        FormBuilder.get()
                .addLabel("Title", propertiesPanel)
                .addLastField(titleField, propertiesPanel);

        InputField<String> descriptionField = createDescriptionInputField();
        FormBuilder.get()
                .addLabel("Description", propertiesPanel)
                .addLastField(descriptionField, propertiesPanel);

        JBPanel propertiesBoxPanel = ContainerFactory.createPropertiesBoxPanel(propertiesPanel);

        addTab("Flow properties", Icons.FileTypeFlow, propertiesBoxPanel, "Flow properties");
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
