package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.action.Strategy;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.commons.ScopeUtils.*;

/**
 * Strategy which can be applied to preceding nodes of type ScopedGraphNode.
 * Note that only a scoped node might have more than one successor.
 */
public class PrecedingScopedNode implements Strategy {

    private final PlaceholderProvider placeholderProvider;
    private final ScopedGraphNode closestPrecedingNode;
    private final Graphics2D graphics;
    private final FlowGraph graph;
    private final Point dropPoint;

    PrecedingScopedNode(@NotNull FlowGraph graph,
                        @NotNull Point dropPoint,
                        @NotNull ScopedGraphNode scopedNode,
                        @NotNull Graphics2D graphics,
                        @NotNull PlaceholderProvider placeholderProvider) {
        this.placeholderProvider = placeholderProvider;
        this.closestPrecedingNode = scopedNode;
        this.dropPoint = dropPoint;
        this.graphics = graphics;
        this.graph = graph;
    }

    // We must find where the drop point is for each successor of preceding the scoped node.
    // The preceding scope node might have 'n' children, therefore for each children we need to find
    // the exact point where the  drop point is. For a given child, the drop point might be:
    // * In the topmost area: in this case the node must be added above the current child.
    // * In the center area: in this case the node must be added before the current child.
    // * In the bottom area: in this case the node must be added below the current child and above the next child.
    // Special cases are considered when the drop point is at the top of the topmost child
    // or at the bottom of the bottommost child.
    //
    // |-----------| yTopScopeBound (if successorIndex == 0)
    // |           |
    // |-----------| yTopTopBound
    // |   top-0   |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |  center-0 |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom-0 |
    // |-----------| yBottomBottomBound == yTopTopBound
    // |   top-1   |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |  center-1 |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom-1 |
    // |-----------| yBottomBottomBound
    // |           |
    // |-----------| yBottomScopeBound (if successor index == successors.length - 1)
    @Override
    public void execute(GraphNode node) {
        List<GraphNode> successors = graph.successors(closestPrecedingNode);

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {

            GraphNode successor = successors.get(successorIndex);

            // Adds a node at index "successorIndex", the existing nodes are shifted down.
            if (isInsideTopArea(graph, successors, successorIndex, closestPrecedingNode, dropPoint, graphics)) {
                // Node -> successorIndex
                // The other ones are shifted down
                if (closestPrecedingNode.isSuccessorAllowedTop(graph, node, successorIndex)) {
                    graph.add(closestPrecedingNode, node, successorIndex);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));

                    closestPrecedingNode.onSuccessorAdded(graph, node, successorIndex);
                    if (IsScopedGraphNode.of(node)) {
                        node.onAdded(graph, placeholderProvider);
                    }
                }
                break; // we stop if we find an area matching the position.

                // Replaces the first node at index "successorIndex".
            } else if (isInsideCenterArea(successor, dropPoint, graphics)) {
                if (closestPrecedingNode.isSuccessorAllowedBefore(graph, node, successorIndex)) {
                    graph.remove(closestPrecedingNode, successor);
                    graph.add(closestPrecedingNode, node, successorIndex);
                    graph.add(node, successor);
                    closestPrecedingNode.addToScope(node);

                    closestPrecedingNode.onSuccessorAdded(graph, node, successorIndex);
                    if (IsScopedGraphNode.of(node)) {
                        node.onAdded(graph, placeholderProvider);
                    }
                }
                break; // we stop if we find an area matching the position.

                // Adds a node next to the current index. Existing nodes at "successorIndex + 1" are shifted down.
            } else if (isInsideBottomArea(graph, successors, successorIndex, closestPrecedingNode, dropPoint, graphics)) {
                if (closestPrecedingNode.isSuccessorAllowedBottom(graph, node, successorIndex + 1)) {
                    graph.add(closestPrecedingNode, node, successorIndex + 1);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));

                    closestPrecedingNode.onSuccessorAdded(graph, node, successorIndex);
                    if (IsScopedGraphNode.of(node)) {
                        node.onAdded(graph, placeholderProvider);
                    }
                }
                break; // we stop if we find an area matching the position.
            }
        }
    }
}
