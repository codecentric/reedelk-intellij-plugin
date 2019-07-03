package com.esb.plugin.component.type.fork;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.AbstractScopedGraphNode;
import com.esb.plugin.editor.designer.widget.SelectedBox;
import com.esb.plugin.editor.designer.widget.VerticalDivider;
import com.esb.plugin.editor.designer.widget.VerticalDividerArrows;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public class ForkNode extends AbstractScopedGraphNode {

    public static final int NODE_HEIGHT = 140;
    public static final int NODE_WIDTH = 130;

    private static final int VERTICAL_DIVIDER_X_OFFSET = 7;
    private static final int SELECTED_BOX_WIDTH = NODE_WIDTH - 14;

    private final SelectedBox selectedBox;
    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;


    public ForkNode(ComponentData componentData) {
        super(componentData);
        this.selectedBox = new SelectedBox();
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows = new VerticalDividerArrows(VERTICAL_DIVIDER_X_OFFSET);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // Draw the background box of this selected component
        if (isSelected()) {
            selectedBox.setPosition(x() - Half.of(SELECTED_BOX_WIDTH), y());
            selectedBox.draw(graphics, SELECTED_BOX_WIDTH, bottomHalfHeight(graphics));
        }

        super.draw(graph, graphics, observer);

        verticalDivider.draw(graph, graphics);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
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

}
