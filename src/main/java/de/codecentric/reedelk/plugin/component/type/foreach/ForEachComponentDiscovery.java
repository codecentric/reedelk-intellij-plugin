package de.codecentric.reedelk.plugin.component.type.foreach;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyJoinAware;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputForEach;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ForEachComponentDiscovery extends DiscoveryStrategyJoinAware {

    public ForEachComponentDiscovery(@NotNull Module module,
                                     @NotNull PlatformModuleService moduleService,
                                     @NotNull TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom, GraphNode successor) {
        Optional<PreviousComponentOutput> previous = discover(context, nodeWeWantOutputFrom);
        return previous.map(PreviousComponentOutputForEach::new);
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
