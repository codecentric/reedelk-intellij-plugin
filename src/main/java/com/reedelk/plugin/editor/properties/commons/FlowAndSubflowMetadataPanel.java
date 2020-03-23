package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.editor.properties.renderer.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class FlowAndSubflowMetadataPanel extends DisposableTabbedPane {

    private final transient FlowSnapshot snapshot;

    public FlowAndSubflowMetadataPanel(FlowSnapshot snapshot) {
        super(JTabbedPane.LEFT);
        this.snapshot = snapshot;
        initialize();
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    private void initialize() {
        GenericTab genericTab = new GenericTab(() -> {
            DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());
            InputField<String> titleField = createTitleInputField();
            FormBuilder.get()
                    .addLabel(message("flow.metadata.title"), propertiesPanel)
                    .addLastField(titleField, propertiesPanel);

            InputField<String> descriptionField = createDescriptionInputField();
            FormBuilder.get()
                    .addLabel(message("flow.metadata.description"), propertiesPanel)
                    .addLastField(descriptionField, propertiesPanel);
            return propertiesPanel;
        });
        String tabName = message("properties.panel.tab.title.flow");
        addTab(tabName, genericTab);
        setTabComponentAt(0, new TabLabelVertical(tabName));
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
