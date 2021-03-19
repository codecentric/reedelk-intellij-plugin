package de.codecentric.reedelk.plugin.component.type.router;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyOneOfAware;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RouterComponentDiscovery extends DiscoveryStrategyOneOfAware {

    public RouterComponentDiscovery(@NotNull Module module,
                                    @NotNull PlatformModuleService moduleService,
                                    @NotNull TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode, GraphNode successor) {
        // The previous output of the router component is the output of its previous component.
        // This is because the router forwards the input message to the outbound component matching
        // the given condition.
        return discover(context, currentNode);
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
