package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputOneOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouterComponentDiscovery extends GenericComponentDiscovery {

    public RouterComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode) {
        // Skip one
        return discover(context, currentNode);
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
