package com.esb.plugin.component.type.unknown;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

public class UnknownConnectionBuilder implements ConnectorBuilder {
    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {
        throw new UnsupportedOperationException();
    }
}
