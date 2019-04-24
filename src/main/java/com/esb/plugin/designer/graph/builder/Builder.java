package com.esb.plugin.designer.graph.builder;


import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.openapi.module.Module;
import org.json.JSONObject;

public interface Builder {

    Drawable build(Module module, Drawable parent, JSONObject componentDefinition, FlowGraph graph);

}
