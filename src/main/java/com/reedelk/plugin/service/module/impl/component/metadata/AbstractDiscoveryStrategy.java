package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;

import java.util.Optional;

public abstract class AbstractDiscoveryStrategy implements DiscoveryStrategy {

    protected final PlatformComponentService componentService;
    protected final TrieMapWrapper typeAndAndTries;
    protected final Module module;

    public AbstractDiscoveryStrategy(Module module, PlatformComponentService componentService, TrieMapWrapper typeAndAndTries) {
        this.componentService = componentService;
        this.typeAndAndTries = typeAndAndTries;
        this.module = module;
    }

    protected Optional<? extends ComponentOutputDescriptor> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, componentService, typeAndAndTries, context, target);
    }
}
