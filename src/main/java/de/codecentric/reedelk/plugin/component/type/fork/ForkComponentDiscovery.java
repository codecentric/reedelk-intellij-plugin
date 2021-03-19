package de.codecentric.reedelk.plugin.component.type.fork;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyJoinAware;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ForkComponentDiscovery extends DiscoveryStrategyJoinAware {

    public ForkComponentDiscovery(@NotNull Module module,
                                  @NotNull PlatformModuleService moduleService,
                                  @NotNull TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode, GraphNode successor) {
        // The output of the fork is the output of the previous component because the fork just
        // clones the input message to the outbound components.
        return discover(context, currentNode);
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
