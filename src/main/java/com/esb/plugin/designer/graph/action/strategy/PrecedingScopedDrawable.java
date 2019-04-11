package com.esb.plugin.designer.graph.action.strategy;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class PrecedingScopedDrawable extends AbstractAddStrategy {


    public PrecedingScopedDrawable(FlowGraph graph, Point dropPoint, Connector connector) {
        super(graph, dropPoint, connector);
    }

    // It is the only type of node with potentially many successors.
    @Override
    public void execute(Drawable input) {
        checkState(input instanceof ScopedDrawable, "Strategy only accepts ScopedDrawable");

        ScopedDrawable closestPrecedingDrawable = (ScopedDrawable) input;

        List<Drawable> successors = graph.successors(closestPrecedingDrawable);

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // We search the node on which the drop point lies on the Y axis.
            // If one is found, we stop.
            Drawable successor = successors.get(successorIndex);
            int yTopBound = successor.y() - Tile.HALF_HEIGHT + Math.floorDiv(Tile.HEIGHT, 4);
            int yBottomBound = successor.y() - Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4);

            if (dropPoint.y > yBottomBound && dropPoint.y < yTopBound) {
                connector.addPredecessor(closestPrecedingDrawable, successorIndex);
                addToScopeIfNeeded(closestPrecedingDrawable);
                connectCommonSuccessorsOf(closestPrecedingDrawable);
                return;
            }
        }

        // If we get to this point, then the node to add is in the LAST position
        // OR it is the first node to be added to the choice component.
        connector.addPredecessor(closestPrecedingDrawable);
        addToScopeIfNeeded(closestPrecedingDrawable);
        connectCommonSuccessorsOf(closestPrecedingDrawable);
    }

    private void connectCommonSuccessorsOf(ScopedDrawable closestPrecedingNode) {
        ScopeUtilities
                .listFirstDrawablesOutsideScope(graph, closestPrecedingNode)
                .forEach(connector::addSuccessor);
    }

}
