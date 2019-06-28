package com.esb.plugin.component.type.router;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.router.functions.SyncConditionAndRoutePairs;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.DrawableListener;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.editor.designer.widget.Icon;
import com.esb.plugin.editor.designer.widget.VerticalDivider;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.system.component.Placeholder;
import com.esb.system.component.Router;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RouterNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";
    public static final int HEIGHT = 140;
    public static final int WIDTH = 170;

    private static final int VERTICAL_DIVIDER_X_OFFSET = 60;
    private static final int ICON_X_OFFSET = 30;


    private final Icon icon;
    private final VerticalDivider verticalDivider;

    public RouterNode(ComponentData componentData) {
        super(componentData);
        this.icon = new Icon(componentData);
        this.verticalDivider = new VerticalDivider(this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        icon.draw(graphics, observer);
        verticalDivider.draw(graph, graphics, observer);
    }

    @Override
    public void mouseMoved(DrawableListener listener, MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        if (icon.contains(x, y)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mousePressed(DrawableListener listener, MouseEvent event) {
        // Nothing to do
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
        drawVerticalDividerArrows(graph, graphics);
    }

    @Override
    public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

    }

    @Override
    public Point getTargetArrowEnd() {
        return icon.getTargetArrowEnd();
    }

    @Override
    public Point getSourceArrowStart() {
        return icon.getSourceArrowStart();
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(x, y);
    }

    @Override
    public int bottomHalfHeight(Graphics2D graphics) {
        return Half.of(HEIGHT);
    }

    @Override
    public int topHalfHeight(Graphics2D graphics) {
        return Half.of(HEIGHT);
    }

    @Override
    public int height(Graphics2D graphics) {
        return HEIGHT;
    }

    @Override
    public int width(Graphics2D graphics) {
        return WIDTH;
    }

    @Override
    public boolean isSuccessorAllowed(FlowGraph graph, GraphNode successor, int index) {
        List<GraphNode> successors = graph.successors(this);
        return index < successors.size();
    }

    @Override
    public void commit(FlowGraph graph, Module module) {
        // If successors is empty, lets add a placeholder

        List<RouterConditionRoutePair> routerConditionRoutePairs = listConditionRoutePairs();
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

            routerConditionRoutePairs.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION, placeholder));
        }


        List<RouterConditionRoutePair> updatedConditions =
                SyncConditionAndRoutePairs.getUpdatedPairs(graph, this, routerConditionRoutePairs);
        ComponentData component = componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, updatedConditions);
    }

    private List<RouterConditionRoutePair> listConditionRoutePairs() {
        ComponentData component = componentData();
        List<RouterConditionRoutePair> conditionRoutePair = component.get(DATA_CONDITION_ROUTE_PAIRS);
        if (conditionRoutePair == null) {
            conditionRoutePair = new LinkedList<>();
            component.set(DATA_CONDITION_ROUTE_PAIRS, conditionRoutePair);
        }
        return conditionRoutePair;
    }

    private boolean isDefaultRoute(GraphNode target) {
        return listConditionRoutePairs()
                .stream()
                .anyMatch(routerConditionRoutePair ->
                        Router.DEFAULT_CONDITION.equals(routerConditionRoutePair.getCondition()) &&
                                routerConditionRoutePair.getNext() == target);
    }

    // TODO: This one should be a class
    private void drawVerticalDividerArrows(FlowGraph graph, Graphics2D graphics) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Half.of(width(graphics));
        int verticalX = x() - 60 + halfWidth;

        // We only need to draw arrows if they are inside the scope

        List<GraphNode> successors = graph.successors(this);
        for (GraphNode successor : successors) {
            // We only draw connections to successors within this scope drawable
            if (!getScope().contains(successor)) continue;

            Point targetBaryCenter = successor.getTargetArrowEnd();
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);

            Arrow arrow = new Arrow(sourceBaryCenter, targetBaryCenter);
            arrow.draw(graphics);

            if (isDefaultRoute(successor)) {
                graphics.setColor(Color.GRAY);
                graphics.drawString("otherwise", verticalX + 6, sourceBaryCenter.y + 14);
            }
        }
    }
}
