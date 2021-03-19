package de.codecentric.reedelk.plugin.component.type.placeholder;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlaceholderComponentDiscovery implements DiscoveryStrategy {

    public PlaceholderComponentDiscovery(@NotNull Module module,
                                         @NotNull PlatformModuleService moduleService,
                                         @NotNull TypeAndTries typeAndAndTries) {
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode, GraphNode successor) {
        return Optional.empty();
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        throw new UnsupportedOperationException();
    }
}
