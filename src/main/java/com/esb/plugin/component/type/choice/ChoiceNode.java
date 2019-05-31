package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.editor.designer.widget.Icon;
import com.esb.plugin.editor.designer.widget.VerticalDivider;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.Choice;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.*;

public class ChoiceNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private static final int VERTICAL_DIVIDER_X_OFFSET = 60;
    private static final int ICON_X_OFFSET = 30;
    private static final int HEIGHT = 140;
    private static final int WIDTH = 170;

    private static final String EMPTY_CONDITION = "";

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

        drawVerticalDividerArrows(graph, graphics, observer);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        icon.setPosition(x - ICON_X_OFFSET, y);
        verticalDivider.setPosition(x - VERTICAL_DIVIDER_X_OFFSET, y);
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
    public void commit(FlowGraph graph) {
        List<GraphNode> successors = graph.successors(this);

        // Compute new Choice condition
        List<ChoiceConditionRoutePair> oldConditions = listConditionRoutePairs();
        List<ChoiceConditionRoutePair> updatedConditions = new LinkedList<>();

        Map<GraphNode, ChoiceConditionRoutePair> existingConditionMap = new HashMap<>();
        successors.forEach(successor -> findTargetPair(successor, oldConditions)
                .ifPresent(choiceConditionRoutePair -> existingConditionMap.put(successor, choiceConditionRoutePair)));

        for (int i = 0; i < successors.size(); i++) {
            GraphNode successor = successors.get(i);
            if (existingConditionMap.containsKey(successor)) {
                ChoiceConditionRoutePair pair = existingConditionMap.get(successor);
                updatedConditions.add(pair);
            } else {
                Collection<ChoiceConditionRoutePair> usedConditionRoutes = new HashSet<>(existingConditionMap.values());
                usedConditionRoutes.addAll(updatedConditions);
                Optional<ChoiceConditionRoutePair> oneAtIndexNotUsedYet = findOneAtIndexNotUsedYet(i, oldConditions, usedConditionRoutes);
                if (oneAtIndexNotUsedYet.isPresent()) {
                    ChoiceConditionRoutePair pair = oneAtIndexNotUsedYet.get();
                    pair.setNext(successor);
                    updatedConditions.add(pair);
                } else {
                    updatedConditions.add(new ChoiceConditionRoutePair(EMPTY_CONDITION, successor));
                }
            }
        }

        ComponentData component = componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, updatedConditions);
    }

    private Optional<ChoiceConditionRoutePair> findOneAtIndexNotUsedYet(int i, List<ChoiceConditionRoutePair> choiceConditionRoutePairs, Collection<ChoiceConditionRoutePair> alreadyUsed) {
        if (i < choiceConditionRoutePairs.size()) {
            ChoiceConditionRoutePair pair = choiceConditionRoutePairs.get(i);
            if (!alreadyUsed.contains(pair)) return Optional.of(pair);
        }
        return Optional.empty();
    }

    private Optional<ChoiceConditionRoutePair> findTargetPair(GraphNode target, List<ChoiceConditionRoutePair> oldConditions) {
        return oldConditions
                .stream()
                .filter(choiceConditionRoutePair -> choiceConditionRoutePair.getNext() == target)
                .findFirst();
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
