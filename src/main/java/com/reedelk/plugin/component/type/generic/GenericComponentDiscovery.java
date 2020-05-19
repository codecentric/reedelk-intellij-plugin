package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.*;
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
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode) {
        String componentFullyQualifiedName = currentNode.componentData().getFullyQualifiedName();
        ComponentDescriptor componentDescriptor = moduleService.componentDescriptorOf(componentFullyQualifiedName);

        ComponentOutputDescriptor componentOutput = componentDescriptor.getOutput();
        // If the component output has not been defined for this component we return empty.
        if (componentOutput == null) return Optional.of(
                new PreviousComponentOutputDefault(
                        singletonList(DEFAULT_ATTRIBUTES),
                        singletonList(DEFAULT_PAYLOAD), DEFAULT_DESCRIPTION));

        // Here you might have components having 'PreviousComponent' only on attributes (e.g payload set),
        // this means that the payload type would be from the current component output but the attributes
        // need to be discovered from the previous(es) components.
        // There might also be cases where the Component defines as payload and attribute types
        // PreviousComponents. In this case the payload and attributes must be discovered from
        // previous(es) components, for example: Logger component.
        PreviousComponentOutput payload = payload(currentNode, context, componentOutput);
        PreviousComponentOutput attributes = attributes(currentNode, context, componentOutput);

        return Optional.of(new PreviousComponentOutputCompound(attributes, payload));
    }


    private PreviousComponentOutput payload(GraphNode currentNode,
                                            ComponentContext context,
                                            ComponentOutputDescriptor currentOutput) {
        List<String> payload = currentOutput.getPayload();
        if (payload.contains(ComponentOutput.PreviousComponent.class.getName())) {
            if (context.predecessors(currentNode).isEmpty()) {
                // If this is root we stop and do not look for the previous component,
                // this is because otherwise we might continuously loop to the previous component
                // when all the components specified the 'PreviousComponent' constraint in the payload type.
                return new PreviousComponentOutputDefault(
                        singletonList(DEFAULT_ATTRIBUTES),
                        singletonList(DEFAULT_PAYLOAD),
                        DEFAULT_DESCRIPTION);
            }

            // We need to recursively go back in the graph if the user specified that the payload
            // type must be taken from the previous component.
            return discover(context, currentNode)
                    .orElse(new PreviousComponentOutputDefault(
                            singletonList(DEFAULT_ATTRIBUTES),
                            singletonList(DEFAULT_PAYLOAD),
                            DEFAULT_DESCRIPTION));

        } else if (payload.contains(ComponentOutput.InferFromDynamicProperty.class.getName())) {

            ComponentData componentData = currentNode.componentData();
            String dynamicPropertyName = currentOutput.getDynamicPropertyName();

            String dynamicExpression = null;
            if (componentData.has(dynamicPropertyName)) {
                dynamicExpression = componentData.get(dynamicPropertyName);
            }

            // Infer from dynamic expression. The input of the dynamic expression is the
            // previous component.
            PreviousComponentOutput componentOutput = discover(context, currentNode)
                    .orElse(new PreviousComponentOutputDefault(
                            singletonList(DEFAULT_ATTRIBUTES),
                            singletonList(DEFAULT_PAYLOAD),
                            DEFAULT_DESCRIPTION));

            return new PreviousComponentOutputInferFromDynamicExpression(componentOutput, dynamicExpression);

        } else {
            return new PreviousComponentOutputDefault(currentOutput.getAttributes(), payload, currentOutput.getDescription());
        }
    }

    private PreviousComponentOutput attributes(GraphNode currentNode, ComponentContext context, ComponentOutputDescriptor currentOutput) {
        if (currentOutput.getAttributes().contains(ComponentOutput.PreviousComponent.class.getName())) {
            // We need to recursively go back in the graph if the user specified that the attributes
            // type must be taken from the previous component.
            return discover(context, currentNode)
                    .orElse(new PreviousComponentOutputDefault(
                            singletonList(DEFAULT_ATTRIBUTES),
                            singletonList(DEFAULT_PAYLOAD),
                            DEFAULT_DESCRIPTION));
        } else {
            return new PreviousComponentOutputDefault(
                    currentOutput.getAttributes(),
                    currentOutput.getPayload(),
                    currentOutput.getDescription());
        }
    }

    private static final ComponentOutputDescriptor DEFAULT;
    private static final String DEFAULT_DESCRIPTION = StringUtils.EMPTY;
    private static final String DEFAULT_PAYLOAD = Object.class.getName();
    private static final String DEFAULT_ATTRIBUTES = MessageAttributes.class.getName();
    static {
        DEFAULT = new ComponentOutputDescriptor();
        DEFAULT.setPayload(singletonList(DEFAULT_PAYLOAD));
        DEFAULT.setAttributes(singletonList(DEFAULT_ATTRIBUTES));
    }
}
