package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.choice.functions.SyncConditionAndRoutePairs;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.DrawableListener;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.editor.designer.widget.Icon;
import com.esb.plugin.editor.designer.widget.VerticalDivider;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.system.component.Choice;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChoiceNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private static final int VERTICAL_DIVIDER_X_OFFSET = 60;
    private static final int ICON_X_OFFSET = 30;
    private static final int HEIGHT = 140;
    private static final int WIDTH = 170;

    private final Icon icon;
    private final VerticalDivider verticalDivider;

    public ChoiceNode(ComponentData componentData) {
        super(componentData);
        this.icon = new Icon(componentData);
        this.verticalDivider = new VerticalDivider(this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        icon.draw(graph, graphics, observer);
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
        drawVerticalDividerArrows(graph, graphics, observer);
    }

    @Override
    public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

    }

    @Override
    public Point getBarycenter() {
        return icon.getBarycenter();
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(x, y);
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

        List<ChoiceConditionRoutePair> choiceConditionRoutePairs = listConditionRoutePairs();
        if (getScope().isEmpty()) {
            GraphNode placeholder = GraphNodeFactory.get(module, Placeholder.class.getName());
            graph.add(placeholder);
            List<GraphNode> successors = graph.successors(this);
            List<GraphNode> toRemove = new ArrayList<>(successors);
            toRemove.forEach(node -> {
                graph.remove(ChoiceNode.this, node);
                graph.add(placeholder, node);
            });
            graph.add(this, placeholder);
            addToScope(placeholder);

            choiceConditionRoutePairs.add(new ChoiceConditionRoutePair(Choice.DEFAULT_CONDITION, placeholder));
        }


        List<ChoiceConditionRoutePair> updatedConditions =
                SyncConditionAndRoutePairs.getUpdatedPairs(graph, this, choiceConditionRoutePairs);
        ComponentData component = componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, updatedConditions);
    }

    private List<ChoiceConditionRoutePair> listConditionRoutePairs() {
        ComponentData component = componentData();
        List<ChoiceConditionRoutePair> conditionRoutePair = component.get(DATA_CONDITION_ROUTE_PAIRS);
        if (conditionRoutePair == null) {
            conditionRoutePair = new LinkedList<>();
            component.set(DATA_CONDITION_ROUTE_PAIRS, conditionRoutePair);
        }
        return conditionRoutePair;
    }

    private boolean isDefaultRoute(GraphNode target) {
        return listConditionRoutePairs()
                .stream()
                .anyMatch(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getCondition().equals(Choice.DEFAULT_CONDITION) &&
                                choiceConditionRoutePair.getNext() == target);
    }

    private void drawVerticalDividerArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Math.floorDiv(width(graphics), 2);
        int verticalX = x() - 60 + halfWidth;

        List<GraphNode> successors = graph.successors(this);
        for (GraphNode successor : successors) {

            Point targetBaryCenter = successor.getBarycenter();
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);
            Point target = getTarget(graphics, successor);

            Arrow arrow = new Arrow(sourceBaryCenter, target);
            arrow.draw(graphics);

            if (isDefaultRoute(successor)) {
                graphics.setColor(Color.GRAY);
                graphics.drawString("otherwise", verticalX + 6, sourceBaryCenter.y + 14);
            }
        }
    }
}
