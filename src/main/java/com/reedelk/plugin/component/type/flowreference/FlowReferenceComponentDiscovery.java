package com.reedelk.plugin.component.type.flowreference;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.type.flowreference.discovery.SubflowDeserializer;
import com.reedelk.plugin.component.type.flowreference.discovery.SubflowGraphAwareContext;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.reedelk.runtime.commons.JsonParser.FlowReference;

public class FlowReferenceComponentDiscovery implements DiscoveryStrategy {

    protected final PlatformModuleService moduleService;
    protected final TypeAndTries typeAndAndTries;
    protected final Module module;

    public FlowReferenceComponentDiscovery(@NotNull Module module,
                                           @NotNull PlatformModuleService moduleService,
                                           @NotNull TypeAndTries typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
        this.moduleService = moduleService;
        this.module = module;
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom, GraphNode successor) {

        ComponentData componentData = nodeWeWantOutputFrom.componentData();

        if (!componentData.has(FlowReference.ref())) return Optional.empty();

        String flowReferenceId = componentData.get(FlowReference.ref());

        Optional<FlowGraph> deSerializedSubflow = deserializeSubflowBy(flowReferenceId);

        return deSerializedSubflow.flatMap(subflowGraph -> {

            SubflowGraphAwareContext newContext = new SubflowGraphAwareContext(subflowGraph, nodeWeWantOutputFrom);

            return discover(newContext, nodeWeWantOutputFrom);
        });
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        throw new UnsupportedOperationException();
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }

    Optional<FlowGraph> deserializeSubflowBy(String flowReferenceId) {
        SubflowDeserializer deserializer = new SubflowDeserializer(module, flowReferenceId);
        return deserializer.deserialize();
    }
}
