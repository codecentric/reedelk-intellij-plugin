package com.esb.plugin.component.choice;

import com.esb.component.FlowReference;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.flowreference.FlowReferenceNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopedNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

import java.util.List;

public class ChoiceConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        FlowReferenceNode placeholder = GraphNodeFactory.get(module, FlowReference.class.getName());

        ChoiceNode choice = (ChoiceNode) componentToAdd;
        choice.addToScope(placeholder);

        FlowGraph subGraph = new FlowGraphImpl();
        subGraph.root(choice);
        subGraph.add(componentToAdd, placeholder);

        ComponentData componentData = componentToAdd.component();
        List<ChoiceConditionRoutePair> nodeConditionMap = (List<ChoiceConditionRoutePair>) componentData.get(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS);

        // TODO: This is duplicated code
        nodeConditionMap
                .stream()
                .filter(choiceConditionRoutePair -> choiceConditionRoutePair.getNext() == placeholder)
                .findFirst()
                .ifPresent(choiceConditionRoutePair -> choiceConditionRoutePair.setCondition("otherwise"));

        return new ScopedNodeConnector(graph, subGraph);
    }
}
