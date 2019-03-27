package com.esb.plugin.graph.handler;

import com.esb.plugin.graph.FlowGraph;
import org.json.JSONObject;

public class DrawableForkHandler implements DrawableComponentHandler<DrawableFork> {

    @Override
    public DrawableFork handle(Drawable parent, JSONObject component, FlowGraph graph) {
        return null;
    }

}
