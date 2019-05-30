package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSubGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopedNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.system.component.Choice;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;

import java.util.LinkedList;
import java.util.List;

import static com.esb.plugin.component.type.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;

public class ChoiceConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        GraphNode placeholder = GraphNodeFactory.get(module, Placeholder.class.getName());

        ChoiceNode choice = (ChoiceNode) componentToAdd;
        choice.addToScope(placeholder);

        FlowSubGraph scopeInitialSubGraph = new FlowSubGraph();
        scopeInitialSubGraph.root(choice);
        scopeInitialSubGraph.add(choice, placeholder);

        List<ChoiceConditionRoutePair> choiceDefaultConditionPairs = new LinkedList<>();
        choiceDefaultConditionPairs.add(new ChoiceConditionRoutePair(Choice.DEFAULT_CONDITION, placeholder));

        ComponentData componentData = componentToAdd.componentData();
        componentData.set(DATA_CONDITION_ROUTE_PAIRS, choiceDefaultConditionPairs);

        return new ScopedNodeConnector(graph, scopeInitialSubGraph);
    }
}
