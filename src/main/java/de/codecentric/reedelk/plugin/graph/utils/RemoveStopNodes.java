package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static de.codecentric.reedelk.plugin.graph.utils.GraphNodeFilters.ScopedGraphNodes;
import static de.codecentric.reedelk.plugin.graph.utils.GraphNodeFilters.StopNodes;

public class RemoveStopNodes {

    private RemoveStopNodes() {
    }

    /**
     * This class removes from a given flow graph all the nodes having
     * type 'StopNode' and bridges the incoming node to the successor
     * of the 'StopNode' node.
     * <p>
     * The graph does not display to the user 'Stop' nodes since they
     * are only used to build the graph.
     *
     * @param graph the graph from which we want to remove stop nodes.
     * @return the original graph without stop nodes. Preceding nodes of stop nodes
     * are connected to the successors of the removed stop nodes.
     */
    public static FlowGraph from(FlowGraph graph) {
        FlowGraph copy = graph.copy();

        graph.breadthFirstTraversal(currentNode ->

                getSuccessorStopNode(graph, currentNode).ifPresent(stopNode ->

                        getFirstNonStopSuccessor(copy, stopNode).ifPresent(nonStopSuccessor -> {

                            // Remove all inbound/outbound edges from/to stop nodes

                            // Connect current node with the next node after stop
                            copy.add(currentNode, nonStopSuccessor);

                            // Remove edge between current node and stop
                            copy.remove(currentNode, stopNode);

                        })));

        Collection<StopNode> stopNodes = StopNodes.from(graph.nodes());

        // Remove all stop nodes from the graph
        stopNodes.forEach(copy::remove);

        // Remove from any scoped node the stop nodes
        ScopedGraphNodes.from(graph.nodes())
                .forEach(scopedNode ->
                        stopNodes.forEach(scopedNode::removeFromScope));

        return copy;
    }

    private static Optional<GraphNode> getFirstNonStopSuccessor(FlowGraph graph, StopNode stop) {
        List<GraphNode> successors = graph.successors(stop);
        checkState(successors.isEmpty() || successors.size() == 1, "Expected only zero or one successor");
        if (successors.isEmpty()) {
            return Optional.empty();
        } else {
            GraphNode successor = successors.get(0);
            return successor instanceof StopNode ?
                    getFirstNonStopSuccessor(graph, (StopNode) successor) :
                    Optional.of(successor);
        }
    }

    private static Optional<StopNode> getSuccessorStopNode(FlowGraph graph, GraphNode node) {
        return StopNodes.from(graph.successors(node)).stream().findFirst();
    }
}
