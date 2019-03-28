package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.DrawableFork;
import org.json.JSONObject;

public class DrawableForkHandler implements DrawableComponentHandler<DrawableFork> {

    @Override
    public DrawableFork handle(Drawable parent, JSONObject component, FlowGraph graph) {
        return null;
    }

}
