package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.Tile;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.Drawable;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.editor.designer.widget.Icon;
import com.esb.plugin.editor.designer.widget.VerticalDivider;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.Choice;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

public class ChoiceNode extends AbstractScopedGraphNode {

    private static final int ICON_X_OFFSET = 30;

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private static final String EMPTY_CONDITION = "";

    private final Drawable verticalDivider;

    private final Icon icon;


    public ChoiceNode(ComponentData componentData) {
        super(componentData);
        this.icon = new Icon(componentData);
        this.verticalDivider = new VerticalDivider(this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        icon.setPosition(x() - ICON_X_OFFSET, y());
        icon.draw(graph, graphics, observer);

        verticalDivider.setPosition(x() - 60, y());
        verticalDivider.draw(graph, graphics, observer);

        drawVerticalDividerArrows(graph, graphics, observer);

        super.draw(graph, graphics, observer);
    }


    private void drawVerticalDividerArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Math.floorDiv(width(graphics), 2);
        int verticalX = x() - 60 + halfWidth - 6;

        List<GraphNode> successors = graph.successors(this);
        for (GraphNode successor : successors) {

            Point targetBaryCenter = successor.getBarycenter(graphics, observer);
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);
            Point target = getTarget(graphics, successor, observer);

            Arrow arrow = new Arrow(sourceBaryCenter, target);
            arrow.draw(graphics);

            if (isDefaultRoute(successor)) {
                graphics.setColor(Color.GRAY);
                graphics.drawString("otherwise", verticalX + 6, sourceBaryCenter.y + 13);
            }
        }
    }


    @Override
    public Point getBarycenter(Graphics2D graphics, ImageObserver observer) {
        return icon.getBarycenter(graphics, observer);
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(observer, x, y);
    }

    private boolean isDefaultRoute(GraphNode target) {
        return listConditionRoutePairs()
                .stream()
                .anyMatch(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getCondition().equals(Choice.DEFAULT_CONDITION) &&
                                choiceConditionRoutePair.getNext() == target);
    }

    @Override
    public void onSuccessorAdded(FlowGraph graph, GraphNode successor) {
        executeIfRouteToNodeNotPresent(successor, conditionRoutePairs ->
                conditionRoutePairs.add(new ChoiceConditionRoutePair(EMPTY_CONDITION, successor)));
    }

    @Override
    public void onSuccessorAdded(FlowGraph graph, GraphNode successor, int index) {
        if (index == graph.successors(this).size() - 1) {
            List<ChoiceConditionRoutePair> choiceConditionRoutePairs = listConditionRoutePairs();
            choiceConditionRoutePairs
                    .stream()
                    .filter(choiceConditionRoutePair -> choiceConditionRoutePair.getCondition().equals(Choice.DEFAULT_CONDITION))
                    .findFirst()
                    .ifPresent(choiceConditionRoutePair -> choiceConditionRoutePair.setNext(successor));
        } else {
            executeIfRouteToNodeNotPresent(successor, conditionRoutePairs ->
                    conditionRoutePairs.add(index, new ChoiceConditionRoutePair(EMPTY_CONDITION, successor)));
        }
    }

    @Override
    public boolean isSuccessorAllowed(FlowGraph graph, GraphNode successor, int index) {
        List<GraphNode> successors = graph.successors(this);
        return index < successors.size();
    }

    @Override
    public int width(Graphics2D graphics) {
        return Tile.WIDTH + 60;
    }


    interface Action {
        void execute(List<ChoiceConditionRoutePair> conditionRoutePairs);
    }

    private void executeIfRouteToNodeNotPresent(GraphNode target, Action action) {
        List<ChoiceConditionRoutePair> conditionRoutePairList = listConditionRoutePairs();
        boolean isPresent = conditionRoutePairList.stream()
                .anyMatch(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getNext() == target);
        if (!isPresent) {
            action.execute(conditionRoutePairList);
        }
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
}
