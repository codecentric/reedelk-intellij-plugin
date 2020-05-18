package com.reedelk.plugin.component.type.foreach;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForEachComponentDiscovery extends AbstractDiscoveryStrategy {

    public ForEachComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    // TODO: Bug: For each followed by fork, displays List instead of Object
    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom) {

        // If previous is list, we must extract...
        // TODO: Note that the previous could also be need to add type List<Object>, then it would work for generics too... like when you exit from a fork
        Optional<PreviousComponentOutput> previous = discover(context, nodeWeWantOutputFrom);
        return previous.map(PreviousComponentOutputForEach::new);
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

        return Optional.of(new PreviousComponentOutputJoin(outputs));
    }
}
