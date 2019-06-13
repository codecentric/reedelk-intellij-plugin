package com.esb.plugin.component.type.flowreference;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.service.module.SubflowService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.esb.internal.commons.JsonParser.FlowReference;

public class FlowReferencePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public FlowReferencePropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        List<ComponentPropertyDescriptor> descriptors = new ArrayList<>();
        componentData.getDataProperties()
                .stream()
                .filter(propertyName -> !propertyName.equals(FlowReference.ref()))
                .forEach(property ->
                        componentData.getPropertyDescriptor(property)
                                .ifPresent(descriptors::add));

        JBPanel genericPropertiesPanel = createPropertiesPanelFrom(descriptors, componentData);


        List<String> strings = SubflowService.getInstance(module).listSubflows();
        JComboBox<String> subflowsList = new ComboBox<>();
        strings.forEach(subflowsList::addItem);

        FormBuilder.get()
                .addLabel("Subflow", genericPropertiesPanel)
                .addLastField(subflowsList, genericPropertiesPanel);

        return genericPropertiesPanel;
    }
}
