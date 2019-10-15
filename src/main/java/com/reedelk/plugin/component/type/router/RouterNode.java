package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.router.functions.IsDefaultRoute;
import com.reedelk.plugin.component.type.router.functions.ListConditionRoutePairs;
import com.reedelk.plugin.component.type.router.functions.SyncConditionAndRoutePairs;
import com.reedelk.plugin.editor.designer.AbstractScopedGraphNode;
import com.reedelk.plugin.editor.designer.widget.VerticalDivider;
import com.reedelk.plugin.editor.designer.widget.VerticalDividerArrows;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.GraphNodeFactory;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.runtime.component.Placeholder;
import com.reedelk.runtime.component.Router;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

public class RouterNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";
    public static final int NODE_HEIGHT = 140;
    private static final int NODE_WIDTH = 170;

    private static final int VERTICAL_DIVIDER_X_OFFSET = 45;
    private static final int ICON_X_OFFSET = 20;

    //private final SelectedBox selectedBox;
    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;

    public RouterNode(ComponentData componentData) {
        super(componentData);
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows =
                new VerticalDividerArrows(VERTICAL_DIVIDER_X_OFFSET, new RouterOnProcessSuccessor());
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
        verticalDivider.setPosition(x - VERTICAL_DIVIDER_X_OFFSET, y);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);
        verticalDividerArrows.draw(this, graph, graphics);
    }

    @Override
    public int height(Graphics2D graphics) {
        return NODE_HEIGHT;
    }

    @Override
    public int width(Graphics2D graphics) {
        return NODE_WIDTH;
    }

    @Override
    public boolean isSuccessorAllowed(FlowGraph graph, GraphNode successor, int index) {
        List<GraphNode> successors = graph.successors(this);
        return index < successors.size();
    }

    @Override
    public void commit(FlowGraph graph, Module module) {
        // TODO: Extract this function

        List<RouterConditionRoutePair> routerConditionRoutePairs =
                ListConditionRoutePairs.of(componentData());

        // If the scope is empty, the router node always  has  a placeholder
        // because a router node must have at least  one component in it.
        if (getScope().isEmpty()) {
            GraphNode placeholder = GraphNodeFactory.get(module, Placeholder.class.getName());
            graph.add(placeholder);
            List<GraphNode> successors = graph.successors(this);
            List<GraphNode> toRemove = new ArrayList<>(successors);
            toRemove.forEach(node -> {
                graph.remove(RouterNode.this, node);
                graph.add(placeholder, node);
            });
            graph.add(this, placeholder);
            addToScope(placeholder);

            routerConditionRoutePairs.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.value(), placeholder));
        }

        List<RouterConditionRoutePair> updatedConditions =
                SyncConditionAndRoutePairs.getUpdatedPairs(graph, this, routerConditionRoutePairs);
        ComponentData component = componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, updatedConditions);
    }

    @Override
    protected void drawRemoveComponentIcon(Graphics2D graphics, ImageObserver observer) {
        int topRightX = x() + Half.of(icon.width()) - ICON_X_OFFSET;
        int topRightY = y() - icon.topHalfHeight(graphics) + Half.of(removeComponentIcon.height());
        removeComponentIcon.setPosition(topRightX, topRightY);
        removeComponentIcon.draw(graphics, observer);
    }

    class RouterOnProcessSuccessor implements VerticalDividerArrows.OnProcessSuccessor {

        private static final int DEFAULT_ROUTE_TEXT_LEFT_PADDING = 6;
        private static final int DEFAULT_ROUTE_TEXT_TOP_PADDING = 14;

        @Override
        public void onProcess(ScopedGraphNode parent, GraphNode successor, Graphics2D graphics) {
            if (IsDefaultRoute.of(parent, successor)) {
                int halfWidth = Half.of(parent.width(graphics));
                int verticalX = parent.x() - VERTICAL_DIVIDER_X_OFFSET + halfWidth;
                graphics.setColor(Colors.TEXT_DEFAULT_ROUTE);
                Point targetArrowEnd = successor.getTargetArrowEnd();
                graphics.drawString(Labels.ROUTER_DEFAULT_ROUTE, verticalX + DEFAULT_ROUTE_TEXT_LEFT_PADDING,
                        targetArrowEnd.y + DEFAULT_ROUTE_TEXT_TOP_PADDING);
            }
        }
    }
}
