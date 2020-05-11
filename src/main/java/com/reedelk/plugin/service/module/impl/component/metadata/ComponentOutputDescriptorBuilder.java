package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.runtime.api.annotation.ComponentOutput;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ComponentOutputDescriptorBuilder {

    private final PlatformComponentServiceImpl componentService;

    public ComponentOutputDescriptorBuilder(PlatformComponentServiceImpl componentService) {
        this.componentService = componentService;
    }

    // The component output might descriptor might not be present.
    public Optional<ComponentOutputDescriptor> build(ContainerContext context) {
        GraphNode componentGraphNode = context.predecessor();
        String componentFullyQualifiedName =
                componentGraphNode.componentData().getFullyQualifiedName();
        ComponentDescriptor actualComponentDescriptor =
                componentService.componentDescriptorFrom(componentFullyQualifiedName);

        ComponentOutputDescriptor actualComponentOutput = actualComponentDescriptor.getOutput();
        if (actualComponentOutput == null) return Optional.empty();

        // Process payload types:
        ComponentOutputDescriptor payloadRealTypes = findPayloadRealTypes(componentGraphNode, context, actualComponentOutput);
        List<String> processedPayloadTypes = payloadRealTypes.getPayload();
        String processedDescription = payloadRealTypes.getDescription();

        // Process attribute type:
        ComponentOutputDescriptor processedDescriptor = findAttributeRealType(componentGraphNode, context, actualComponentOutput);
        String processedAttributeType = processedDescriptor.getAttributes();

        ComponentOutputDescriptor finalComponentOutput = new ComponentOutputDescriptor();
        finalComponentOutput.setPayload(processedPayloadTypes);
        finalComponentOutput.setAttributes(processedAttributeType);
        finalComponentOutput.setDescription(processedDescription);
        return Optional.of(finalComponentOutput);
    }

    private ComponentOutputDescriptor findPayloadRealTypes(GraphNode currentNode, ContainerContext context, ComponentOutputDescriptor previous) {
        if (previous.getPayload().contains(ComponentOutput.PreviousComponent.class.getName())) {
            // We need to recursively go back in the graph and find one node with a real type.
            GraphNode predecessor = context.predecessor(currentNode);
            String newFullyQualifiedName = predecessor.componentData().getFullyQualifiedName();
            ComponentDescriptor componentDescriptor = componentService.componentDescriptorFrom(newFullyQualifiedName);
            ComponentOutputDescriptor componentOutputDescriptor = componentDescriptor.getOutput();
            if (componentOutputDescriptor == null) return defaultDescriptor();
            // We need to recursively go back until we find a type
            return findPayloadRealTypes(predecessor, context, componentOutputDescriptor);
        } else {
            return previous;
        }
    }

    private ComponentOutputDescriptor defaultDescriptor() {
        ComponentOutputDescriptor defaultDescriptor = new ComponentOutputDescriptor();
        defaultDescriptor.setPayload(Collections.singletonList(Object.class.getName()));
        return defaultDescriptor;
    }

    private ComponentOutputDescriptor findAttributeRealType(GraphNode currentNode, ContainerContext context, ComponentOutputDescriptor previous) {
        if (ComponentOutput.PreviousComponent.class.getName().equals(previous.getAttributes())) {
            // We need to recursively go back in the graph and find one node with a real type.
            GraphNode predecessor = context.predecessor(currentNode);
            String newFullyQualifiedName = predecessor.componentData().getFullyQualifiedName();
            ComponentDescriptor componentDescriptor = componentService.componentDescriptorFrom(newFullyQualifiedName);
            ComponentOutputDescriptor componentOutputDescriptor = componentDescriptor.getOutput();
            if (componentOutputDescriptor == null) return defaultDescriptor();
            // We need to recursively go back until we find a type
            return findAttributeRealType(predecessor, context, componentOutputDescriptor);
        } else {
            return previous;
        }
    }
}
