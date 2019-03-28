package com.esb.plugin.designer.graph.builder;


import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import org.json.JSONObject;

public interface DrawableComponentHandler<T extends Drawable> {

    T handle(Drawable parent, JSONObject implementorDefinition, FlowGraph graph);

}
