package com.esb.plugin.component.type.flowreference;

import com.esb.internal.commons.Preconditions;
import com.esb.internal.commons.StringUtils;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.service.module.SubflowService;
import com.esb.plugin.service.module.impl.SubflowMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.esb.internal.commons.JsonParser.FlowReference;

public class FlowReferencePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public FlowReferencePropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        String reference = componentData.get(FlowReference.ref());

        List<ComponentPropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();
        List<ComponentPropertyDescriptor> filteredDescriptors = descriptors
                .stream()
                .filter(descriptor -> !FlowReference.ref().equals(descriptor.getPropertyName()))
                .collect(Collectors.toList());

        JBPanel genericPropertiesPanel = createPropertiesPanelFrom(filteredDescriptors, componentData);

        Optional<ComponentPropertyDescriptor> propertyDescriptor = componentData.getPropertyDescriptor(FlowReference.ref());
        Preconditions.checkState(propertyDescriptor.isPresent(), "Reference property descriptor must not be null");
        ComponentPropertyDescriptor referencePropertyDescriptor = propertyDescriptor.get();

        JComboBox<SubflowMetadata> subflowsList = buildSubflowSelectorCombo(componentData, reference);

        FormBuilder.get()
                .addLabel(referencePropertyDescriptor.getDisplayName(), genericPropertiesPanel)
                .addLastField(subflowsList, genericPropertiesPanel);

        return genericPropertiesPanel;
    }

    @NotNull
    private JComboBox<SubflowMetadata> buildSubflowSelectorCombo(ComponentData componentData, String reference) {
        JComboBox<SubflowMetadata> subflowsList = new ComboBox<>();
        subflowsList.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String title = value.getTitle();
            return new JLabel(title);
        });

        List<SubflowMetadata> subflowsMetadata = SubflowService.getInstance(module).listSubflows();
        subflowsMetadata.add(UNSELECTED_SUBFLOW);

        SubflowMetadata matchingMetadata = findMatchingMetadata(subflowsMetadata, reference);
        if (!subflowsMetadata.contains(matchingMetadata)) {
            subflowsMetadata.add(matchingMetadata);
        }

        subflowsMetadata.forEach(subflowsList::addItem);
        subflowsList.setSelectedItem(matchingMetadata);
        subflowsList.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                SubflowMetadata item = (SubflowMetadata) event.getItem();
                componentData.set(FlowReference.ref(), item.getId());
                snapshot.onDataChange();
            }
        });

        return subflowsList;
    }

    private SubflowMetadata findMatchingMetadata(List<SubflowMetadata> subflowsMetadata, String reference) {
        if (!StringUtils.isBlank(reference)) {
            return subflowsMetadata.stream()
                    .filter(subflowMetadata -> subflowMetadata.getId().equals(reference))
                    .findFirst()
                    .orElseGet(() -> new UnresolvedSubflowMetadata(reference));
        }
        return UNSELECTED_SUBFLOW;
    }

    private static final SubflowMetadata UNSELECTED_SUBFLOW = new UnselectedSubflowMetadata();

    static class UnresolvedSubflowMetadata extends SubflowMetadata {
        UnresolvedSubflowMetadata(String id) {
            super(id, String.format("Unresolved (%s)", id));
        }
    }

    static class UnselectedSubflowMetadata extends SubflowMetadata {
        UnselectedSubflowMetadata() {
            super(FlowReferenceNode.DEFAULT_FLOW_REFERENCE, "<Not selected>");
        }
    }
}
