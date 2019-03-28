package com.esb.plugin.designer.graph.builder;


import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import org.json.JSONObject;

public interface Builder<T extends Drawable> {

    T build(Drawable parent, JSONObject componentDefinition, FlowGraph graph);

}
