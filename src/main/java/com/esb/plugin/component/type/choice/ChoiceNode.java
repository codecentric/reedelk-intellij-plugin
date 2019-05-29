package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.Tile;
import com.esb.plugin.editor.designer.drawables.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.drawables.Arrow;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.Choice;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

public class ChoiceNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private static final String EMPTY_CONDITION = "";

    public ChoiceNode(ComponentData componentData) {
        super(componentData);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        int iconDrawableHeight = icon.height(graphics);
        int halfIconDrawableHeight = Math.floorDiv(iconDrawableHeight, 2);

        int componentTitleHeight = componentTitle.height(graphics);
        int halfComponentTitleHeight = Math.floorDiv(componentTitleHeight, 2);

        int componentDescriptionHeight = componentDescription.height(graphics);
        int halfComponentDescriptionHeight = Math.floorDiv(componentDescriptionHeight, 2);

        int totalHeight = iconDrawableHeight + componentTitleHeight + componentDescriptionHeight;
        int halfTotalHeight = Math.floorDiv(totalHeight, 2);

        // Center icon
        int centerIconY = y() - halfTotalHeight + halfIconDrawableHeight;
        icon.setPosition(x() - 30, centerIconY);

        // Center title below icon
        int centerTitleY = y() - halfTotalHeight + iconDrawableHeight + halfComponentTitleHeight;
        componentTitle.setPosition(x() - 30, centerTitleY);

        // Center description below title
        int centerDescriptionY = y() - halfTotalHeight + iconDrawableHeight + componentTitleHeight + halfComponentDescriptionHeight;
        componentDescription.setPosition(x() - 30, centerDescriptionY);


        drawConnections(graph, graphics, observer);

        icon.draw(graph, graphics, observer);
        componentTitle.draw(graph, graphics, observer);
        componentDescription.draw(graph, graphics, observer);

        verticalDivider.setPosition(x() - 60, y());
        verticalDivider.draw(graph, graphics, observer);

        scopeBox.draw(graph, graphics, observer);
    }

    @Override
    public void drag(int x, int y) {
        // Choice Node cannot be dragged
    }


    @Override
    public void dragging() {
        // Choice Node cannot be dragged
    }

    @Override
    public void drop() {
        // Choice Node cannot be dragged
    }

    @Override
    public void selected() {
        super.selected();
        scopeBox.selected();
    }

    @Override
    public void unselected() {
        super.unselected();
        scopeBox.unselected();
    }

    @Override
    protected void drawConnections(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // Draw arrows -> perpendicular to the vertical bar.
        int halfWidth = Math.floorDiv(width(graphics), 2);
        int verticalX = x() - 60 + halfWidth - 6;

        List<GraphNode> successors = graph.successors(this);
        for (GraphNode successor : successors) {

            Point targetBaryCenter = successor.getBarycenter(graphics);
            Point sourceBaryCenter = new Point(verticalX, targetBaryCenter.y);
            Point target = getTarget(graphics, successor);

            Arrow arrow = new Arrow(sourceBaryCenter, target);
            arrow.draw(graphics);

            if (isDefaultRoute(successor)) {
                graphics.setColor(Color.GRAY);
                graphics.drawString("otherwise", verticalX + 6, sourceBaryCenter.y - 4);
            }
        }

        drawEndOfScopeArrow(graph, graphics);
    }

    @Override
    public Point getBarycenter(Graphics2D graphics) {
        Point barycenter = super.getBarycenter(graphics);
        barycenter.x = barycenter.x - 30;
        return barycenter;
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
        executeIfRouteToNodeNotPresent(successor, conditionRoutePairs ->
                conditionRoutePairs.add(index, new ChoiceConditionRoutePair(EMPTY_CONDITION, successor)));
    }

    @Override
    public void onSuccessorRemoved(FlowGraph graph, GraphNode successor) {
        listConditionRoutePairs()
                .removeIf(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getNext() == successor);
    }

    @Override
    public boolean isSuccessorAllowed(FlowGraph graph, GraphNode successor, int index) {
        List<GraphNode> successors = graph.successors(this);
        return index < successors.size();
    }

    @Override
    public boolean isSuccessorAllowed(FlowGraph graph, GraphNode successor) {
        throw new UnsupportedOperationException();
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
