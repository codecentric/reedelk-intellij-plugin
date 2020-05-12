package com.reedelk.plugin.component.type.flowreference;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.type.flowreference.widget.SubflowSelector;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.JsonParser.FlowReference;
import static java.util.stream.Collectors.toList;

public class FlowReferencePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public FlowReferencePropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JComponent render(GraphNode node) {
        ComponentData componentData = node.componentData();

        Optional<PropertyDescriptor> propertyDescriptor = componentData.getPropertyDescriptor(FlowReference.ref());
        if (!propertyDescriptor.isPresent()) {
            throw new IllegalStateException("Reference property descriptor must not be null");
        }

        String componentFullyQualifiedName = componentData.getFullyQualifiedName();

        ContainerContext context = new ContainerContext(snapshot, node, componentFullyQualifiedName);

        Supplier<JComponent> componentSupplier = () -> {

            String propertyName = FlowReference.ref();

            PropertyTypeDescriptor propertyType = propertyDescriptor.get().getType();

            PropertyAccessor referencePropertyAccessor = context.propertyAccessorOf(propertyName, propertyType, componentData);

            List<PropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();
            List<PropertyDescriptor> filteredDescriptors = descriptors
                    .stream()
                    .filter(descriptor -> !FlowReference.ref().equals(descriptor.getName()))
                    .collect(toList());

            DisposablePanel panel = new PropertiesPanelHolder(module, context, componentData, filteredDescriptors);

            PropertyDescriptor referencePropertyDescriptor = propertyDescriptor.get();

            SubflowSelector selector = buildSubflowSelectorCombo(referencePropertyAccessor);

            PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(referencePropertyDescriptor);
            FormBuilder.get()
                    .addLabel(propertyTitleLabel, panel)
                    .addLastField(selector, panel);

            return panel;
        };

        String defaultTabKey = message("properties.panel.tab.title.general");

        Map<String, Supplier<JComponent>> tabAndComponentSupplier = ImmutableMap.of(defaultTabKey, componentSupplier);

        PropertiesPanelTabbedPanel tabbedPanel = new PropertiesPanelTabbedPanel(componentData, tabAndComponentSupplier, context);

        return new PropertiesThreeComponentsSplitter(module, context, tabbedPanel);
    }


    @NotNull
    private SubflowSelector buildSubflowSelectorCombo(PropertyAccessor referencePropertyAccessor) {
        List<SubflowMetadata> subflowsMetadata = SubflowService.getInstance(module).listSubflows();
        subflowsMetadata.add(UNSELECTED_SUBFLOW);

        // If find matching metadata is unresolved, then we must add it to the collection.
        SubflowMetadata matchingMetadata = findMatchingMetadata(subflowsMetadata, referencePropertyAccessor.get());
        if (!subflowsMetadata.contains(matchingMetadata)) {
            subflowsMetadata.add(matchingMetadata);
        }

        SubflowSelector selector = new SubflowSelector(subflowsMetadata);
        selector.setSelectedItem(matchingMetadata);
        selector.addSelectListener(subflowMetadata ->
                referencePropertyAccessor.set(subflowMetadata.getId()));
        return selector;
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
