package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

public interface ConnectorBuilder {

    Connector build(Module module, FlowGraph graph, GraphNode componentToAdd);

}
