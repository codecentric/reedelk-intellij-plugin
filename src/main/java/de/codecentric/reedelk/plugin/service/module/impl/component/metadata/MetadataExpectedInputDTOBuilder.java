package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentInputDescriptor;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeProxy;

import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class MetadataExpectedInputDTOBuilder {

    private final PlatformModuleService moduleService;
    private final TypeAndTries typeAndTrie;

    public MetadataExpectedInputDTOBuilder(PlatformModuleService moduleService, TypeAndTries typeAndTrie) {
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
                    .map(payloadType -> TypeProxy.create(payloadType).resolve(typeAndTrie).toSimpleName(typeAndTrie))
                    .collect(joining(", "));
            return new MetadataExpectedInputDTO(payload, description);

        }).orElse(null);
    }
}
