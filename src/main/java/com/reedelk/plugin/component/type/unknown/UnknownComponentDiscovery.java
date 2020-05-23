package com.reedelk.plugin.component.type.unknown;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UnknownComponentDiscovery implements DiscoveryStrategy {

    public UnknownComponentDiscovery(@NotNull Module module,
                                     @NotNull PlatformModuleService moduleService,
                                     @NotNull TypeAndTries typeAndAndTries) {
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom) {
        // The output of this component can not be determined because
        // this component is unknown, therefore we return empty.
        return Optional.empty();
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        throw new UnsupportedOperationException();
    }
}
