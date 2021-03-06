package de.codecentric.reedelk.plugin.editor.properties.commons;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.commons.PredefinedPropertyDescriptor;

import java.awt.*;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class FlowAndSubflowMetadataPanel extends DisposableTabbedPane {

    private final transient FlowSnapshot snapshot;

    public FlowAndSubflowMetadataPanel(FlowSnapshot snapshot) {
        super(LEFT);
        this.snapshot = snapshot;
        initialize();
    }

    private void initialize() {
        GenericTab genericTab = new GenericTab(() -> {

            DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());

            // Title
            PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(PredefinedPropertyDescriptor.FLOW_TITLE);
            InputField<String> titleField = createTitleInputField(PredefinedPropertyDescriptor.FLOW_TITLE);
            FormBuilder.get()
                    .addLabel(propertyTitleLabel, propertiesPanel)
                    .addLastField(titleField, propertiesPanel);

            // Label
            PropertyTitleLabel propertyDescriptionLabel = new PropertyTitleLabel(PredefinedPropertyDescriptor.FLOW_DESCRIPTION);
            InputField<String> descriptionField = createDescriptionInputField(PredefinedPropertyDescriptor.FLOW_DESCRIPTION);
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
}
