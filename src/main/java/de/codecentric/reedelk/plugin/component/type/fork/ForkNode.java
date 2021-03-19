package de.codecentric.reedelk.plugin.component.type.fork;

import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.editor.designer.AbstractScopedGraphNode;
import de.codecentric.reedelk.plugin.editor.designer.arrow.VerticalDividerArrows;
import de.codecentric.reedelk.plugin.editor.designer.misc.VerticalDivider;
import de.codecentric.reedelk.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public class ForkNode extends AbstractScopedGraphNode {

    private static final int VERTICAL_DIVIDER_X_OFFSET = 7;

    private final VerticalDivider verticalDivider;
    private final VerticalDividerArrows verticalDividerArrows;

    public ForkNode(ComponentData componentData) {
        super(componentData);
        this.verticalDivider = new VerticalDivider(this);
        this.verticalDividerArrows = new VerticalDividerArrows(VERTICAL_DIVIDER_X_OFFSET);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
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
        return 130;
    }

    @Override
    public int verticalDividerXOffset() {
        return VERTICAL_DIVIDER_X_OFFSET;
    }
}
