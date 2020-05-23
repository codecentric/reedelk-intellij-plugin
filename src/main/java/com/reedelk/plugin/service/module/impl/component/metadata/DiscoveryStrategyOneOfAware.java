package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DiscoveryStrategyOneOfAware implements DiscoveryStrategy {

    protected final PlatformModuleService moduleService;
    protected final TypeAndTries typeAndAndTries;
    protected final Module module;

    public DiscoveryStrategyOneOfAware(@NotNull Module module,
                                      @NotNull PlatformModuleService moduleService,
                                      @NotNull TypeAndTries typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
        this.moduleService = moduleService;
        this.module = module;
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        // The output is one of the branches evaluated true
        List<PreviousComponentOutput> outputs = new ArrayList<>();
        List<GraphNode> lastNodesOfScope = context.listLastNodesOfScope(scopedGraphNode);

        for (GraphNode lastNodeOfScope : lastNodesOfScope) {

            // Extract a method for each node
            Optional<ScopedGraphNode> theScopeOfLastNode = context.findScopeOf(lastNodeOfScope);
            if (theScopeOfLastNode.isPresent() && theScopeOfLastNode.get() != scopedGraphNode) {
                // In a different scope and there is no JOIN in the middle need to lookup for the scope
                String innerScopeFullyQualifiedName = theScopeOfLastNode.get().componentData().getFullyQualifiedName();
                DiscoveryStrategy strategy =
                        DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, innerScopeFullyQualifiedName);
                strategy.compute(context, theScopeOfLastNode.get()).ifPresent(outputs::add);

            } else {
                String nodeFullyQualifiedName = lastNodeOfScope.componentData().getFullyQualifiedName();
                DiscoveryStrategy strategy =
                        DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, nodeFullyQualifiedName);
                strategy.compute(context, lastNodeOfScope).ifPresent(outputs::add);
            }
        }

        return Optional.of(new PreviousComponentOutputOneOf(outputs));
    }
}
