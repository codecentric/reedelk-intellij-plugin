package com.reedelk.plugin.component.type.stop;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractDiscoveryStrategy;

import java.util.Optional;

public class StopComponentDiscovery extends AbstractDiscoveryStrategy {

    public StopComponentDiscovery(Module module, PlatformModuleService moduleService, TrieMapWrapper typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom) {
        throw new UnsupportedOperationException();
    }
}
