package com.esb.plugin.designer.graph.builder;


import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONObject;

public interface Builder<T extends Drawable> {

    T build(Drawable parent, JSONObject componentDefinition, FlowGraph graph);

}
