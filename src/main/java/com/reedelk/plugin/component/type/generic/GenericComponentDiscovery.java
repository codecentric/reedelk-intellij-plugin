package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Pair;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractDiscoveryStrategy;
import com.reedelk.runtime.api.annotation.ComponentOutput;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class GenericComponentDiscovery extends AbstractDiscoveryStrategy {

    public GenericComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, GraphNode currentNode) {
        String componentFullyQualifiedName = currentNode.componentData().getFullyQualifiedName();
        ComponentDescriptor componentDescriptor = moduleService.componentDescriptorOf(componentFullyQualifiedName);

        ComponentOutputDescriptor componentOutput = componentDescriptor.getOutput();
        // If the component output has not been defined for this component we return the default output.
        if (componentOutput == null) return Optional.of(DEFAULT);

        // Here you might have components having 'PreviousComponent' only on attributes (e.g payload set),
        // this means that the payload type would be from the current component output but the attributes
        // need to be discovered from the previous(es) components.
        // There might also be cases where the Component defines as payload and attribute types
        // PreviousComponents. In this case the payload and attributes must be discovered from
        // previous(es) components, for example: Logger component.
        Pair<List<String>, String> payloadTypesAndDescription =
                discoverPayloadTypesAndDescription(currentNode, context, componentOutput);
        String attributeType = discoverAttributeType(currentNode, context, componentOutput);

        ComponentOutputDescriptor finalComponentOutput = new ComponentOutputDescriptor();
        finalComponentOutput.setDescription(payloadTypesAndDescription.second);
        finalComponentOutput.setPayload(payloadTypesAndDescription.first);
        finalComponentOutput.setAttributes(attributeType);
        return Optional.of(finalComponentOutput);
    }


    private Pair<List<String>, String> discoverPayloadTypesAndDescription(GraphNode currentNode, ComponentContext context, ComponentOutputDescriptor currentOutput) {
        if (currentOutput.getPayload().contains(ComponentOutput.PreviousComponent.class.getName())) {
            // We need to recursively go back in the graph if the user specified that the payload
            // type must be taken from the previous component.
            return discover(context, currentNode)
                    .map(descriptor -> Pair.create(descriptor.getPayload(), descriptor.getDescription()))
                    .orElse(Pair.create(singletonList(DEFAULT_PAYLOAD), StringUtils.EMPTY));
        } else {
            return Pair.create(currentOutput.getPayload(), currentOutput.getDescription());
        }
    }

    private String discoverAttributeType(GraphNode currentNode, ComponentContext context, ComponentOutputDescriptor currentOutput) {
        if (ComponentOutput.PreviousComponent.class.getName().equals(currentOutput.getAttributes())) {
            // We need to recursively go back in the graph if the user specified that the attributes
            // type must be taken from the previous component.
            return discover(context, currentNode)
                    .map(ComponentOutputDescriptor::getAttributes)
                    .orElse(DEFAULT_ATTRIBUTES);
        } else {
            return currentOutput.getAttributes();
        }
    }

    private static final ComponentOutputDescriptor DEFAULT;
    private static final String DEFAULT_PAYLOAD = Object.class.getName();
    private static final String DEFAULT_ATTRIBUTES = MessageAttributes.class.getName();
    static {
        DEFAULT = new ComponentOutputDescriptor();
        DEFAULT.setPayload(singletonList(DEFAULT_PAYLOAD));
        DEFAULT.setAttributes(DEFAULT_ATTRIBUTES);
    }
}
