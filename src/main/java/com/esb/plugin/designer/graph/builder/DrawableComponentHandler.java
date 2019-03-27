package com.esb.plugin.designer.graph.builder;


import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.Node;
import org.json.JSONObject;

public interface DrawableComponentHandler<T extends Node> {

    T handle(Node parent, JSONObject implementorDefinition, FlowGraph graph);

}
