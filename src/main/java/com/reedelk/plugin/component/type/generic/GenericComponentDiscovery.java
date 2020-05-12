package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.discovery.DiscoveryStrategyFactory;
import com.reedelk.runtime.api.annotation.ComponentOutput;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GenericComponentDiscovery extends AbstractDiscoveryStrategy {

    public GenericComponentDiscovery(Module module, PlatformComponentService componentService, TrieMapWrapper typeAndAndTries) {
        super(module, componentService, typeAndAndTries);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, GraphNode currentNode) {
        String componentFullyQualifiedName = currentNode.componentData().getFullyQualifiedName();
        ComponentDescriptor componentDescriptor = componentService.componentDescriptorOf(componentFullyQualifiedName);

        ComponentOutputDescriptor componentOutput = componentDescriptor.getOutput();
        // if the component output has not been defined for this component we return empty.
        if (componentOutput == null) return Optional.of(DEFAULT);

        // Process payload types: TODO: this code is not clear. It should be optional
        ComponentOutputDescriptor payloadRealTypes = findPayloadRealTypes(currentNode, context, componentOutput);
        List<String> processedPayloadTypes = payloadRealTypes.getPayload();
        String processedDescription = payloadRealTypes.getDescription();

        // Process attribute type:
        ComponentOutputDescriptor processedDescriptor = findAttributeRealType(currentNode, context, componentOutput);
        String processedAttributeType = processedDescriptor.getAttributes();

        ComponentOutputDescriptor finalComponentOutput = new ComponentOutputDescriptor();
        finalComponentOutput.setPayload(processedPayloadTypes);
        finalComponentOutput.setDescription(processedDescription);
        finalComponentOutput.setAttributes(processedAttributeType);
        return Optional.of(finalComponentOutput);
    }

    private ComponentOutputDescriptor findPayloadRealTypes(GraphNode currentNode, ContainerContext context, ComponentOutputDescriptor currentComponentOutput) {
        if (currentComponentOutput.getPayload().contains(ComponentOutput.PreviousComponent.class.getName())) {
            // We need to recursively go back in the graph and find one node with a real type.
            Optional<? extends ComponentOutputDescriptor> componentOutputDescriptor = DiscoveryStrategyFactory.get(module, componentService, typeAndAndTries, context, currentNode);
            return componentOutputDescriptor.isPresent() ? componentOutputDescriptor.get() : DEFAULT;
        } else {
            return currentComponentOutput;
        }
    }

    private ComponentOutputDescriptor findAttributeRealType(GraphNode currentNode, ContainerContext context, ComponentOutputDescriptor currentComponentOutput) {
        if (ComponentOutput.PreviousComponent.class.getName().equals(currentComponentOutput.getAttributes())) {
            // We need to recursively go back in the graph and find one node with a real type.
            Optional<? extends ComponentOutputDescriptor> componentOutputDescriptor =
                    DiscoveryStrategyFactory.get(module, componentService, typeAndAndTries, context, currentNode);
            return componentOutputDescriptor.isPresent() ? componentOutputDescriptor.get() : DEFAULT;
        } else {
            return currentComponentOutput;
        }
    }

    private static final ComponentOutputDescriptor DEFAULT;
    static {
        DEFAULT = new ComponentOutputDescriptor();
        DEFAULT.setPayload(Collections.singletonList(Object.class.getName()));
        DEFAULT.setAttributes(MessageAttributes.class.getName());
    }
}
