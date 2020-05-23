package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// used by fork and for each for components allowing
// a join component.
public abstract class DiscoveryStrategyJoinAware implements DiscoveryStrategy {

    protected final PlatformModuleService moduleService;
    protected final TypeAndTries typeAndAndTries;
    protected final Module module;

    public DiscoveryStrategyJoinAware(@NotNull Module module,
                                     @NotNull PlatformModuleService moduleService,
                                     @NotNull TypeAndTries typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
        this.moduleService = moduleService;
        this.module = module;
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {

        // The current scope
        Optional<GraphNode> node = context.findFirstNodeOutsideCurrentScope(scopedGraphNode);
        if (node.isPresent()) {
            // If the node is JOIN, multiple messages.
            ComponentType componentType = node.get().getComponentType();
            if (ComponentType.JOIN.equals(componentType)) {
                PreviousComponentOutputMultipleMessages descriptor = new PreviousComponentOutputMultipleMessages();
                return Optional.of(descriptor);
            }
        }

        Set<PreviousComponentOutput> outputs = new HashSet<>();
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

        return Optional.of(new PreviousComponentOutputJoin(outputs));
    }
}
