package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.*;
import com.reedelk.runtime.api.annotation.ComponentOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GenericComponentDiscovery implements DiscoveryStrategy {

    protected final PlatformModuleService moduleService;
    protected final TypeAndTries typeAndAndTries;
    protected final Module module;

    public GenericComponentDiscovery(@NotNull Module module,
                                     @NotNull PlatformModuleService moduleService,
                                     @NotNull TypeAndTries typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
        this.moduleService = moduleService;
        this.module = module;
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode, GraphNode successor) {
        String componentFullyQualifiedName = currentNode.componentData().getFullyQualifiedName();

        ComponentDescriptor componentDescriptor = moduleService.componentDescriptorOf(componentFullyQualifiedName);
        ComponentOutputDescriptor componentOutput = componentDescriptor.getOutput();

        // If the component output has not been defined for this component we default output.
        if (componentOutput == null) {
            return Optional.of(DEFAULT);
        }

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

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        throw new UnsupportedOperationException();
    }

    private PreviousComponentOutput payload(GraphNode currentNode,
                                            ComponentContext context,
                                            ComponentOutputDescriptor currentOutput) {
        List<String> payload = currentOutput.getPayload();
        if (payload.contains(ComponentOutput.PreviousComponent.class.getName())) {
            return handlePreviousComponentOutput(currentNode, context);
        } else if (payload.contains(ComponentOutput.InferFromDynamicProperty.class.getName())) {
            return handleInferFromDynamicProperty(currentNode, context, currentOutput);
        } else {
            return new PreviousComponentOutputDefault(currentOutput.getAttributes(), payload, currentOutput.getDescription());
        }
    }

    @NotNull
    private PreviousComponentOutput attributes(GraphNode currentNode, ComponentContext context, ComponentOutputDescriptor currentOutput) {
        if (currentOutput.getAttributes().contains(ComponentOutput.PreviousComponent.class.getName())) {
            return handlePreviousComponentOutput(currentNode, context);
        } else {
            return new PreviousComponentOutputDefault(
                    currentOutput.getAttributes(),
                    currentOutput.getPayload(),
                    currentOutput.getDescription());
        }
    }

    @NotNull
    private PreviousComponentOutput handleInferFromDynamicProperty(GraphNode currentNode, ComponentContext context, ComponentOutputDescriptor currentOutput) {
        ComponentData componentData = currentNode.componentData();
        String dynamicPropertyName = currentOutput.getDynamicPropertyName();

        String dynamicExpression = null;
        if (componentData.has(dynamicPropertyName)) {
            dynamicExpression = componentData.get(dynamicPropertyName);
        }

        // Infer from dynamic expression. The input of the dynamic expression is the previous component.
        PreviousComponentOutput componentOutput = discover(context, currentNode).orElse(DEFAULT);
        return new PreviousComponentOutputInferFromDynamicExpression(componentOutput, dynamicExpression);
    }

    @NotNull
    private PreviousComponentOutput handlePreviousComponentOutput(GraphNode currentNode, ComponentContext context) {
        if (context.predecessors(currentNode).isEmpty()) {
            // If there are no predecessors it means that the user annotated an inbound component
            // with @ComponentOutput and as payload is asking to take the previous component.
            // This configuration for an inbound component is not possible, and we return the
            // default component output.
            return DEFAULT;
        }
        // We need to recursively go back in the graph if the user specified that the payload
        // type must be taken from the previous component.
        return discover(context, currentNode).orElse(DEFAULT);
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
