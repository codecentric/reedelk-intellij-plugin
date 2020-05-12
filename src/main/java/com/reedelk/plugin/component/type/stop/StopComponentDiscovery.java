package com.reedelk.plugin.component.type.stop;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;

import java.util.Optional;

public class StopComponentDiscovery extends AbstractDiscoveryStrategy {

    public StopComponentDiscovery(Module module, PlatformComponentServiceImpl componentService) {
        super(module, componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        throw new UnsupportedOperationException();
    }
}
