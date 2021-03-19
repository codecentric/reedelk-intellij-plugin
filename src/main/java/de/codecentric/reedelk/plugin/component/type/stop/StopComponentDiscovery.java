package de.codecentric.reedelk.plugin.component.type.stop;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

// The stop component is a technical component only and it cannot be displayed.
public class StopComponentDiscovery implements DiscoveryStrategy {

    public StopComponentDiscovery(@NotNull Module module,
                                  @NotNull PlatformModuleService moduleService,
                                  @NotNull TypeAndTries typeAndAndTries) {
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom, GraphNode successor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        throw new UnsupportedOperationException();
    }
}
