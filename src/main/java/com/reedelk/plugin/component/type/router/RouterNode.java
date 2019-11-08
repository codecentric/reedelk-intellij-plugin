package com.reedelk.plugin.component.type.router;

import com.reedelk.plugin.commons.AddPlaceholder;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.commons.Labels.Placeholder;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.router.functions.IsDefaultRoute;
import com.reedelk.plugin.component.type.router.functions.SyncConditionAndRoutePairs;
import com.reedelk.plugin.editor.designer.AbstractScopedGraphNode;
import com.reedelk.plugin.editor.designer.widget.VerticalDivider;
import com.reedelk.plugin.editor.designer.widget.VerticalDividerArrows;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.function.Consumer;

public class RouterNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private final int nodeHeight = 140;
    private final int nodeWidth = 170;

    private final int verticalDividerXOffset = 45;
    private final int iconXOffset = 20;

    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;

    public RouterNode(ComponentData componentData) {
        super(componentData);
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows =
                new VerticalDividerArrows(verticalDividerXOffset, new RouterOnProcessSuccessor());
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        icon.setPosition(x - iconXOffset, y);
        verticalDivider.setPosition(x - verticalDividerXOffset, y);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);
        verticalDividerArrows.draw(this, graph, graphics);
    }

    @Override
    public int height(Graphics2D graphics) {
        return nodeHeight;
    }

    @Override
    public int width(Graphics2D graphics) {
        return nodeWidth;
    }

    // A RouterNode cannot have a node added below the "otherwise" node.
    @Override
    public boolean isSuccessorAllowedBottom(FlowGraph graph, GraphNode successor, int index) {
        List<GraphNode> successors = graph.successors(this);
        return index < successors.size();
    }

    @Override
    public void onSuccessorRemoved(FlowGraph graph, GraphNode removedNode, int index, PlaceholderProvider placeholderProvider) {
        // If we just removed a successor and the scope is empty, it means that the
        // router node does not have any successor. Since we always want to have a placeholder
        // and never an empty node following a router, we add the placeholder.
        if (getScope().isEmpty()) {
            // We just update conditions -> route  pairs if and only if a node was added. A node
            // might have not been added when a successor has been removed when we remove
            // this scoped node itself. In this case the placeholder provider does not add
            // any placeholder since we are removing the containing scope.
            AddPlaceholder.to(placeholderProvider, Placeholder.DESCRIPTION_ROUTER_OTHERWISE, graph, this, index)
                    .ifPresent((Consumer<GraphNode>) node -> updateConditionRoutePairs(graph));
        } else {
            // If the scope is not empty, we need to update the condition -> route pairs since
            // they might have been changed after removing a successor.
            updateConditionRoutePairs(graph);
        }
    }

    @Override
    public void onSuccessorAdded(FlowGraph graph, GraphNode addedNode, int index) {
        updateConditionRoutePairs(graph);
    }

    /**
     * Callback invoked when this node has been added to the graph.
     */
    @Override
    public void onAdded(FlowGraph graph, PlaceholderProvider placeholderProvider) {
        // If the scope is empty, the router node always  has  a placeholder
        // because a router node must have at least one component in it. The index is therefore always 0.
        if (getScope().isEmpty()) {
            AddPlaceholder.to(placeholderProvider, Placeholder.DESCRIPTION_ROUTER_OTHERWISE, graph, this, 0)
                    .ifPresent(addedPlaceholder -> updateConditionRoutePairs(graph));
        } else {
            updateConditionRoutePairs(graph);
        }
    }

    @Override
    protected void drawRemoveComponentIcon(Graphics2D graphics, ImageObserver observer) {
        int topRightX = x() + Half.of(icon.width()) - iconXOffset;
        int topRightY = y() - icon.topHalfHeight(graphics) + Half.of(removeComponentIcon.height());
        removeComponentIcon.setPosition(topRightX, topRightY);
        removeComponentIcon.draw(graphics, observer);
    }

    private void updateConditionRoutePairs(FlowGraph graph) {
        List<RouterConditionRoutePair> updatedConditions = SyncConditionAndRoutePairs.from(graph, this);
        ComponentData component = componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, updatedConditions);
    }

    class RouterOnProcessSuccessor implements VerticalDividerArrows.OnProcessSuccessor {

        private static final int DEFAULT_ROUTE_TEXT_LEFT_PADDING = 6;
        private static final int DEFAULT_ROUTE_TEXT_TOP_PADDING = 14;

        @Override
        public void onProcess(ScopedGraphNode parent, GraphNode successor, Graphics2D graphics) {
            if (IsDefaultRoute.of(parent, successor)) {
                int halfWidth = Half.of(parent.width(graphics));
                int verticalX = parent.x() - verticalDividerXOffset + halfWidth;
                graphics.setColor(Colors.TEXT_DEFAULT_ROUTE);
                Point targetArrowEnd = successor.getTargetArrowEnd();
                graphics.drawString(Labels.ROUTER_DEFAULT_ROUTE, verticalX + DEFAULT_ROUTE_TEXT_LEFT_PADDING,
                        targetArrowEnd.y + DEFAULT_ROUTE_TEXT_TOP_PADDING);
            }
        }
    }
}
