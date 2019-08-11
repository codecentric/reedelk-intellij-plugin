package com.reedelk.plugin.commons;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class PrintFlowInfo {

    private static final Logger LOG = Logger.getInstance(PrintFlowInfo.class);

    private PrintFlowInfo() {
    }

    public static void debug(FlowGraph graph) {
        StringBuilder infoBuilder = new StringBuilder("\n-------------------- Flow Info --------------------");
        graph.breadthFirstTraversal(node -> {
            infoBuilder.append("\n - ");
            buildNodeInfo(graph, node, infoBuilder);
        });
        infoBuilder.append("\n");
        LOG.info(infoBuilder.toString());
    }

    private static void buildNodeInfo(FlowGraph graph, GraphNode node, StringBuilder infoBuilder) {
        String name = name(node);
        infoBuilder.append(name);

        List<GraphNode> successors = graph.successors(node);
        if (!successors.isEmpty()) {
            infoBuilder
                    .append(" (successors: ")
                    .append(collectAndJoinNodeNames(successors))
                    .append(")");
        }

        List<GraphNode> predecessors = graph.predecessors(node);
        if (!predecessors.isEmpty()) {
            infoBuilder
                    .append(" (predecessors: ")
                    .append(collectAndJoinNodeNames(predecessors))
                    .append(")");
        }

        int x = node.x();
        int y = node.y();
        infoBuilder
                .append(" (coordinates: x=")
                .append(x)
                .append(", y=")
                .append(y)
                .append(")");
    }

    private static String collectAndJoinNodeNames(Collection<GraphNode> nodes) {
        return nodes.stream()
                .map(PrintFlowInfo::name)
                .collect(joining(","));
    }

    public static String name(GraphNode node) {
        return node.componentData().getDisplayName() + "@" + System.identityHashCode(node);
    }
}
