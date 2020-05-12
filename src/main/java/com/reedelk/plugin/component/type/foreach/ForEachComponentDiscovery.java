package com.reedelk.plugin.component.type.foreach;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.completion.Default;
import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.discovery.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.discovery.MessagesComponentOutputDescriptor;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ForEachComponentDiscovery extends AbstractDiscoveryStrategy {

    public ForEachComponentDiscovery(Module module, PlatformComponentServiceImpl componentService, TrieMapWrapper typeAndAndTries) {
        super(module, componentService, typeAndAndTries);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {

        Optional<? extends ComponentOutputDescriptor> componentOutputDescriptor = DiscoveryStrategyFactory.get(context, module, componentService, predecessor, typeAndAndTries);
        if (componentOutputDescriptor.isPresent()) {
            List<String> payload = componentOutputDescriptor.get().getPayload();
            if (payload != null && payload.size() == 1) {
                String precedingPayloadType = payload.get(0);
                Trie orDefault = typeAndAndTries.getOrDefault(precedingPayloadType, Default.UNKNOWN);
                if (StringUtils.isNotBlank(orDefault.listItemType())) {
                    ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
                    descriptor.setPayload(Collections.singletonList(orDefault.listItemType()));
                    descriptor.setAttributes(MessageAttributes.class.getName());
                    return Optional.of(descriptor);
                }
            }
        }

        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(Object.class.getName()));
        descriptor.setAttributes(MessageAttributes.class.getName());
        return Optional.of(descriptor);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, Collection<GraphNode> predecessors) {
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
