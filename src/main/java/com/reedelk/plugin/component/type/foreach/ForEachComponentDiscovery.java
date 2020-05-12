package com.reedelk.plugin.component.type.foreach;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.Default;
import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.MessagesComponentOutputDescriptor;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ForEachComponentDiscovery extends AbstractDiscoveryStrategy {

    public ForEachComponentDiscovery(Module module, PlatformComponentService componentService, TrieMapWrapper typeAndAndTries) {
        super(module, componentService, typeAndAndTries);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom) {
        Optional<? extends ComponentOutputDescriptor> componentOutputDescriptor = discover(context, nodeWeWantOutputFrom);
        if (componentOutputDescriptor.isPresent()) {
            ComponentOutputDescriptor previousComponentOutput = componentOutputDescriptor.get();
            List<String> payload = previousComponentOutput.getPayload();
            if (payload != null && payload.size() == 1) {
                String precedingPayloadType = payload.get(0);
                Trie orDefault = typeAndAndTries.getOrDefault(precedingPayloadType, Default.UNKNOWN);
                ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
                descriptor.setAttributes(previousComponentOutput.getAttributes());
                if (StringUtils.isNotBlank(orDefault.listItemType())) {
                    descriptor.setPayload(Collections.singletonList(orDefault.listItemType()));
                } else {
                    descriptor.setPayload(Collections.singletonList(precedingPayloadType));
                }
                return Optional.of(descriptor);
            }
        }

        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(Object.class.getName()));
        descriptor.setAttributes(MessageAttributes.class.getName());
        return Optional.of(descriptor);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, Collection<GraphNode> predecessors) {
        ComponentType componentClass = context.node().getComponentClass();
        if (ComponentType.JOIN.equals(componentClass)) {
            MessagesComponentOutputDescriptor descriptor = new MessagesComponentOutputDescriptor();
            return Optional.of(descriptor);
        } else {
            ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
            outputDescriptor.setPayload(Collections.singletonList(List.class.getName()));
            outputDescriptor.setAttributes(MessageAttributes.class.getName());
            return Optional.of(outputDescriptor);
        }
    }
}
