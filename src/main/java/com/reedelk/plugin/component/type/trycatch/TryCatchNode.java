package com.reedelk.plugin.component.type.trycatch;

import com.reedelk.plugin.commons.AddPlaceholder;
import com.reedelk.plugin.commons.Labels.Placeholder;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.trycatch.widget.TryCatchVerticalDivider;
import com.reedelk.plugin.component.type.trycatch.widget.TryCatchVerticalDividerArrows;
import com.reedelk.plugin.editor.designer.AbstractScopedGraphNode;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

public class TryCatchNode extends AbstractScopedGraphNode {

    private static final int VERTICAL_DIVIDER_X_OFFSET = 7;

    private final TryCatchVerticalDivider verticalDivider;
    private final TryCatchVerticalDividerArrows verticalDividerArrows;

    public TryCatchNode(ComponentData componentData) {
        super(componentData);
        verticalDivider = new TryCatchVerticalDivider(this);
        verticalDividerArrows = new TryCatchVerticalDividerArrows(this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.drawArrows(graph, graphics, observer);
        verticalDividerArrows.draw(graph, graphics);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        verticalDivider.setPosition(x, y);
        verticalDividerArrows.setPosition(x - VERTICAL_DIVIDER_X_OFFSET, y);
    }

    // A try/catch node can ONLY have two successors. When one of these
    // two successors is being removed a placeholder is added, therefore
    // no additional nodes can be added at the top or at the bottom of
    // any existing successor.
    @Override
    public boolean isSuccessorAllowedTop(FlowGraph graph, GraphNode successor, int index) {
        return false;
    }

    @Override
    public boolean isSuccessorAllowedBottom(FlowGraph graph, GraphNode successor, int index) {
        return false;
    }

    @Override
    public void onSuccessorRemoved(FlowGraph graph, GraphNode removedNode, int index, PlaceholderProvider placeholderProvider) {
        // If we remove a node as a successor of this scoped node, if the number
        // of successors is lower than 2 it means that there is either no flow specified
        // for the try block or the catch block or for both.
        if (graph.successors(this).size() < 2) {
            // If index == 0 we removed try
            // If index == 1 we removed catch
            // In both cases we must add a placeholder node.
            String description = index == 0 ? Placeholder.DESCRIPTION_TRY_CATCH_TRY : Placeholder.DESCRIPTION_TRY_CATCH_CATCH;
            AddPlaceholder.to(placeholderProvider, description, graph, this, index);
        }
    }

    // When we add the Try-Catch node for the first time it does not
    // contain any node in the scope. We must add two placeholders for
    // the try and the catch flows.
    @Override
    public void onAdded(FlowGraph graph, PlaceholderProvider placeholderProvider) {
        if (getScope().isEmpty()) {
            AddPlaceholder.to(placeholderProvider, Placeholder.DESCRIPTION_TRY_CATCH_TRY, graph, this, 0);
            AddPlaceholder.to(placeholderProvider, Placeholder.DESCRIPTION_TRY_CATCH_CATCH, graph, this, 1);
        }
    }

    @Override
    public int height(Graphics2D graphics) {
        return 140;
    }

    @Override
    public int width(Graphics2D graphics) {
        return 130;
    }

    @Override
    public int verticalDividerXOffset() {
        return VERTICAL_DIVIDER_X_OFFSET;
    }
}
