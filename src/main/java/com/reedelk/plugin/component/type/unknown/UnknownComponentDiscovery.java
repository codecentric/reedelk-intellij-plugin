package com.reedelk.plugin.component.type.unknown;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;

import java.util.Optional;

public class UnknownComponentDiscovery extends AbstractDiscoveryStrategy {

    public UnknownComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> computeForScope(ComponentContext context, GraphNode nodeWeWantOutputFrom) {
        // The output of this component can not be determined because
        // this component is unknown, therefore we return empty.
        return Optional.empty();
    }
}
