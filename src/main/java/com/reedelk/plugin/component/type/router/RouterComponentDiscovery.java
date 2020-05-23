package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyOneOfAware;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RouterComponentDiscovery extends DiscoveryStrategyOneOfAware {

    public RouterComponentDiscovery(@NotNull Module module,
                                    @NotNull PlatformModuleService moduleService,
                                    @NotNull TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode) {
        // The previous output of the router component is the output of its previous component.
        // This is because the router forwards the input message to the outbound component matching
        // the given condition.
        return discover(context, currentNode);
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
