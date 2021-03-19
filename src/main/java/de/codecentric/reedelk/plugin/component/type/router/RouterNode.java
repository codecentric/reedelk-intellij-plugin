package de.codecentric.reedelk.plugin.component.type.router;

import de.codecentric.reedelk.plugin.commons.AddPlaceholder;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Fonts;
import de.codecentric.reedelk.plugin.commons.Half;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.type.router.functions.IsDefaultRoute;
import de.codecentric.reedelk.plugin.component.type.router.functions.SyncConditionAndRoutePairs;
import de.codecentric.reedelk.plugin.editor.designer.AbstractScopedGraphNode;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import de.codecentric.reedelk.plugin.editor.designer.arrow.VerticalDividerArrows;
import de.codecentric.reedelk.plugin.editor.designer.misc.VerticalDivider;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class RouterNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private static final int VERTICAL_DIVIDER_X_OFFSET = 45;
    private static final int ICON_X_OFFSET = 20;

    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;

    public RouterNode(ComponentData componentData) {
        super(componentData);
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows =
                new VerticalDividerArrows(VERTICAL_DIVIDER_X_OFFSET, new AddOtherwiseRouteLabelOnVerticalDividerArrow());
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        icon.setPosition(x - ICON_X_OFFSET, y);
        verticalDivider.setPosition(x, y);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);
        verticalDividerArrows.draw(this, graph, graphics);
    }

    @Override
    public int height(Graphics2D graphics) {
        return 140;
    }

    @Override
    public int width(Graphics2D graphics) {
        return 170;
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
            AddPlaceholder.to(placeholderProvider, ReedelkBundle.message("placeholder.description.router.otherwise"), graph, this, index)
                    .ifPresent(node -> updateConditionRoutePairs(graph));
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
            AddPlaceholder.to(placeholderProvider, ReedelkBundle.message("placeholder.description.router.otherwise"), graph, this, 0)
                    .ifPresent(addedPlaceholder -> updateConditionRoutePairs(graph));
        } else {
            updateConditionRoutePairs(graph);
        }
    }

    @Override
    protected void drawRemoveComponentIcon(Graphics2D graphics, ImageObserver observer) {
        int topRightX = x() + Half.of(icon.width()) - ICON_X_OFFSET;
        int topRightY = y() - icon.topHalfHeight() + Half.of(iconRemoveComponent.height());
        iconRemoveComponent.setPosition(topRightX, topRightY);
        iconRemoveComponent.draw(graphics, observer);
    }

    @Override
    public int verticalDividerXOffset() {
        return VERTICAL_DIVIDER_X_OFFSET;
    }

    private void updateConditionRoutePairs(FlowGraph graph) {
        List<RouterConditionRoutePair> updatedConditions = SyncConditionAndRoutePairs.from(graph, this);
        ComponentData component = componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, updatedConditions);
    }

    static class AddOtherwiseRouteLabelOnVerticalDividerArrow implements VerticalDividerArrows.OnProcessSuccessor {

        private static final int DEFAULT_ROUTE_TEXT_LEFT_PADDING = 9;
        private static final int DEFAULT_ROUTE_TEXT_TOP_PADDING = -3;

        @Override
        public void onProcess(ScopedGraphNode parent, GraphNode successor, Graphics2D graphics) {
            if (IsDefaultRoute.of(parent, successor)) {
                int halfWidth = Half.of(parent.width(graphics));
                int verticalX = parent.x() - VERTICAL_DIVIDER_X_OFFSET + halfWidth;

                Point targetArrowEnd = successor.getTargetArrowEnd();

                graphics.setColor(Colors.TEXT_DEFAULT_ROUTE);
                graphics.setFont(Fonts.Router.DEFAULT_ROUTE);
                graphics.drawString(ReedelkBundle.message("router.default.route.name"), verticalX + DEFAULT_ROUTE_TEXT_LEFT_PADDING,
                        targetArrowEnd.y + DEFAULT_ROUTE_TEXT_TOP_PADDING);
            }
        }
    }
}
