package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSubGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopedNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

import java.util.List;

import static com.esb.plugin.component.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.esb.plugin.component.choice.ChoiceNode.DEFAULT_CONDITION_NAME;

public class ChoiceConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        // TODO: Fixme... this should not use flow reference and also should use the factory
        GraphNode placeholder = GraphNodeFactory.get(module, "com.esb.system.component.payload.SetPayload");
        placeholder.componentData().set("payload", "{\"name\":\"Mark\"}");

        ChoiceNode choice = (ChoiceNode) componentToAdd;
        choice.addToScope(placeholder);

        FlowSubGraph scopeInitialSubgraph = new FlowSubGraph();
        scopeInitialSubgraph.root(choice);
        scopeInitialSubgraph.add(componentToAdd, placeholder);

        ComponentData componentData = componentToAdd.componentData();
        List<ChoiceConditionRoutePair> nodeConditionMap =
                (List<ChoiceConditionRoutePair>) componentData.get(DATA_CONDITION_ROUTE_PAIRS);

        // TODO: This is duplicated code
        nodeConditionMap
                .stream()
                .filter(choiceConditionRoutePair -> choiceConditionRoutePair.getNext() == placeholder)
                .findFirst()
                .ifPresent(choiceConditionRoutePair -> choiceConditionRoutePair.setCondition(DEFAULT_CONDITION_NAME));

        return new ScopedNodeConnector(graph, scopeInitialSubgraph);
    }
}
