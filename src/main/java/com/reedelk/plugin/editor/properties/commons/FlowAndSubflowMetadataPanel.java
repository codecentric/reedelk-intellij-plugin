package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.TypePrimitiveDescriptors;
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

    private void initialize() {
        GenericTab genericTab = new GenericTab(() -> {

            DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());

            // Title
            PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(propertyTitleDescriptor);
            InputField<String> titleField = createTitleInputField(propertyTitleDescriptor);
            FormBuilder.get()
                    .addLabel(propertyTitleLabel, propertiesPanel)
                    .addLastField(titleField, propertiesPanel);

            // Label
            PropertyTitleLabel propertyDescriptionLabel = new PropertyTitleLabel(propertyDescriptionDescriptor);
            InputField<String> descriptionField = createDescriptionInputField(propertyDescriptionDescriptor);
            FormBuilder.get()
                    .addLabel(propertyDescriptionLabel, propertiesPanel)
                    .addLastField(descriptionField, propertiesPanel);

            return propertiesPanel;
        });

        String tabName = message("properties.panel.tab.title.flow");
        addTab(tabName, genericTab);
        setTabComponentAt(0, new TabLabelVertical(tabName));
    }

    private InputField<String> createTitleInputField(PropertyDescriptor propertyDescriptor) {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        return createStringInputField(graph.title(), propertyDescriptor.getHintValue(), value -> {
            snapshot.getGraphOrThrowIfAbsent().setTitle((String) value);
            snapshot.onDataChange();
        });
    }

    private InputField<String> createDescriptionInputField(PropertyDescriptor propertyDescriptor) {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();
        return createStringInputField(graph.description(), propertyDescriptor.getHintValue(), value -> {
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

    private static final PropertyDescriptor propertyTitleDescriptor = PropertyDescriptor.builder()
            .description(message("flow.title.description"))
            .displayName(message("flow.metadata.title"))
            .hintValue(message("flow.title.hint"))
            .name("title")
            .type(TypePrimitiveDescriptors.STRING)
            .build();

    private static final PropertyDescriptor propertyDescriptionDescriptor = PropertyDescriptor.builder()
            .description(message("flow.description.description"))
            .displayName(message("flow.metadata.description"))
            .hintValue(message("flow.description.hint"))
            .name("description")
            .type(TypePrimitiveDescriptors.STRING)
            .build();
}
