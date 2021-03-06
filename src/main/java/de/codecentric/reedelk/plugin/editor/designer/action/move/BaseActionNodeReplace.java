package de.codecentric.reedelk.plugin.editor.designer.action.move;

import de.codecentric.reedelk.plugin.editor.designer.action.Action;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.GetSuccessorIndex;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;

import java.util.ArrayList;
import java.util.List;

abstract class BaseActionNodeReplace implements Action {

    private final GraphNode from;
    private final GraphNode to;

    BaseActionNodeReplace(GraphNode from, GraphNode to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute(FlowGraph graph) {
        List<GraphNode> predecessors = new ArrayList<>(graph.predecessors(from));
        List<GraphNode> successors = new ArrayList<>(graph.successors(from));

        // It is root (this could happen in a subflow)
        if (predecessors.isEmpty()) {
            graph.root(to);
        }

        // Remove predecessors edges incoming to 'from'
        // Add predecessors incoming to 'to'
        for (GraphNode predecessor : predecessors) {
            // If the predecessor is a ScopedNode, then we must
            // replace it in the same position index of the 'from' node.
            if (IsScopedGraphNode.of(predecessor)) {

                int index = GetSuccessorIndex.ofScopedNode(graph, (ScopedGraphNode) predecessor, from);

                graph.add(predecessor, to, index);

            } else {
                graph.add(predecessor, to);
            }

            graph.remove(predecessor, from);
        }

        // The same as above but with outgoing
        for (GraphNode successor : successors) {
            graph.remove(from, successor);
            graph.add(to, successor);
        }

        // We must remove the from node from the graph
        graph.remove(from);
    }
}
