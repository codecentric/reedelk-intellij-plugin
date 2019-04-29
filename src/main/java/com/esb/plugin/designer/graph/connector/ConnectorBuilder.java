package com.esb.plugin.designer.graph.connector;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.intellij.openapi.module.Module;

public interface ConnectorBuilder {

    Connector build(Module module, FlowGraph graph, GraphNode componentToAdd);

}
