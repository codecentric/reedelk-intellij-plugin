package com.esb.plugin.component.type.flowreference;

import com.esb.internal.commons.StringUtils;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.type.flowreference.widget.SubflowSelector;
import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.service.module.SubflowService;
import com.esb.plugin.service.module.impl.SubflowMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.esb.internal.commons.JsonParser.FlowReference;

public class FlowReferencePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public FlowReferencePropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        Optional<ComponentPropertyDescriptor> propertyDescriptor = componentData.getPropertyDescriptor(FlowReference.ref());
        if (!propertyDescriptor.isPresent()) {
            throw new IllegalStateException("Reference property descriptor must not be null");
        }

        PropertyAccessor referencePropertyAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(propertyDescriptor.get().getPropertyType())
                .propertyName(FlowReference.ref())
                .dataHolder(componentData)
                .snapshot(snapshot)
                .build();

        List<ComponentPropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();
        List<ComponentPropertyDescriptor> filteredDescriptors = descriptors
                .stream()
                .filter(descriptor -> !FlowReference.ref().equals(descriptor.getPropertyName()))
                .collect(Collectors.toList());

        DisposablePanel genericPropertiesPanel = getDefaultPropertiesPanel(componentData, filteredDescriptors);

        ComponentPropertyDescriptor referencePropertyDescriptor = propertyDescriptor.get();

        SubflowSelector selector = buildSubflowSelectorCombo(referencePropertyAccessor);

        FormBuilder.get()
                .addLabel(referencePropertyDescriptor.getDisplayName(), genericPropertiesPanel)
                .addLastField(selector, genericPropertiesPanel);

        return genericPropertiesPanel;
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
