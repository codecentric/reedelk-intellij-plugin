package com.esb.plugin.designer.graph.handler;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.Node;
import org.json.JSONObject;

public class DrawableForkHandler implements DrawableComponentHandler<DrawableFork> {

    @Override
    public DrawableFork handle(Node parent, JSONObject component, FlowGraph graph) {
        return null;
    }

}
