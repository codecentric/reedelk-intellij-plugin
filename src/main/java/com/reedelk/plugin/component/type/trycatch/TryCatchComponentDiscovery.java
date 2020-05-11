package com.reedelk.plugin.component.type.trycatch;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;

import java.util.Optional;

public class TryCatchComponentDiscovery extends AbstractDiscoveryStrategy {

    public TryCatchComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        // TODO: It depends on the child of the try catch. If the uppermost, then we can reuse the generic,
        //  otherwise it is the exception branch.
        return Optional.empty();
    }
}
