package com.reedelk.plugin.component.type.trycatch;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class TryCatchComponentDiscovery extends AbstractDiscoveryStrategy {

    public TryCatchComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom) {
        List<GraphNode> successors = context.successors(nodeWeWantOutputFrom);
        if (successors.get(0).equals(context.node())) {
            // Try branch (we take the one before the try-catch.
            return discover(context, nodeWeWantOutputFrom);
        } else {
            // We are in the catch
            ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
            descriptor.setPayload(singletonList(Exception.class.getName()));
            descriptor.setAttributes(singletonList(MessageAttributes.class.getName()));
            return Optional.empty();
        }
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(Object.class.getName()));
        descriptor.setAttributes(singletonList(MessageAttributes.class.getName()));
        return Optional.empty();
    }
}
