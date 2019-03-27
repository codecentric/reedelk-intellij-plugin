package com.esb.plugin.graph.handler;

import com.esb.plugin.graph.FlowGraph;
import org.json.JSONObject;

public interface DrawableComponentHandler<T extends Drawable> {

    T handle(Drawable parent, JSONObject implementorDefinition, FlowGraph graph);

}
