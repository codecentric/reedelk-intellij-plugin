package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentInputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeUtils;

import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class MetadataExpectedInputBuilder {

    private final PlatformModuleService moduleService;
    private final TypeAndTries typeAndTrie;

    public MetadataExpectedInputBuilder(PlatformModuleService moduleService, TypeAndTries typeAndTrie) {
        this.moduleService = moduleService;
        this.typeAndTrie = typeAndTrie;
    }

    public MetadataExpectedInputDTO build(ComponentContext context) {
        GraphNode componentGraphNode = context.node();

        String componentFullyQualifiedName =
                componentGraphNode.componentData().getFullyQualifiedName();

        ComponentDescriptor actualComponentDescriptor =
                moduleService.componentDescriptorOf(componentFullyQualifiedName);

        Optional<ComponentInputDescriptor> componentInputDescriptor =
                Optional.ofNullable(actualComponentDescriptor.getInput());

        return componentInputDescriptor.map(descriptor -> {
            String description = descriptor.getDescription();
            String payload = descriptor.getPayload().stream()
                    .map(payloadType -> TypeUtils.toSimpleName(payloadType, typeAndTrie))
                    .collect(joining(", "));
            return new MetadataExpectedInputDTO(payload, description);

        }).orElse(null);
    }
}
