package com.reedelk.plugin.component.type.fork;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyJoinAware;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ForkComponentDiscovery extends DiscoveryStrategyJoinAware {

    public ForkComponentDiscovery(@NotNull Module module,
                                  @NotNull PlatformModuleService moduleService,
                                  @NotNull TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode) {
        // The output of the fork is the output of the previous component because the fork just
        // clones the input message to the outbound components.
        return discover(context, currentNode);
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
