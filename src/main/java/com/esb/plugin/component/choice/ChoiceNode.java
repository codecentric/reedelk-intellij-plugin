package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.canvas.drawables.AbstractScopedGraphNode;
import com.esb.plugin.graph.node.GraphNode;

import java.util.HashMap;
import java.util.Map;

public class ChoiceNode extends AbstractScopedGraphNode {

    private static final String EMPTY_CONDITION = "";

    public ChoiceNode(ComponentData componentData) {
        super(componentData);
    }

    @Override
    public void onSuccessorAdded(GraphNode successor) {
        Map<GraphNode, String> nodeConditionMap = getConditionNodeMap();
        if (!nodeConditionMap.containsKey(successor)) {
            nodeConditionMap.put(successor, EMPTY_CONDITION);
        }
    }

    @Override
    public void onSuccessorRemoved(GraphNode successor) {
        Map<GraphNode, String> nodeConditionMap = getConditionNodeMap();
        nodeConditionMap.remove(successor);
    }

    private Map<GraphNode, String> getConditionNodeMap() {
        ComponentData component = component();
        Map<GraphNode, String> nodeConditionMap = (HashMap<GraphNode, String>) component.get("nodeConditionMap");
        if (nodeConditionMap == null) {
            nodeConditionMap = new HashMap<>();
            component.set("nodeConditionMap", nodeConditionMap);
        }
        return nodeConditionMap;
    }
}
