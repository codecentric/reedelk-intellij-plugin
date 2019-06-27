package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.layout.utils.ComputeMaxHeight;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class InboundLane {

    private static final int DEFAULT_INBOUND_LANE_HEIGHT = 170;

    private final Stroke dashed = new BasicStroke(0.7f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
    private final String INBOUND_STRING = "Event";
    private final int topPadding;
    private final FlowSnapshot snapshot;

    public InboundLane(FlowSnapshot snapshot, int topPadding) {
        this.topPadding = topPadding;
        this.snapshot = snapshot;
    }


    public void draw(Graphics2D graphics) {
        int width = AbstractGraphNode.WIDTH;
        drawVerticalBar(graphics, width);
        drawInboundText(graphics, width);
    }

    private void drawInboundText(Graphics2D graphics, int width) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(INBOUND_STRING, graphics);
        double inboundTextWidth = stringBounds.getWidth();

        Font font = graphics.getFont().deriveFont(20f);
        graphics.setFont(font);
        graphics.drawString(INBOUND_STRING, Half.of(width) - Half.of(inboundTextWidth), Half.of(topPadding));
    }

    private void drawVerticalBar(Graphics2D graphics, int width) {
        int height = topPadding;
        if (snapshot.getGraph().isEmpty()) {
            height += DEFAULT_INBOUND_LANE_HEIGHT;
        } else {
            GraphNode root = snapshot.getGraph().root();
            height += ComputeMaxHeight.of(snapshot.getGraph(), graphics, root);
        }

        graphics.setColor(JBColor.GRAY);
        graphics.setStroke(dashed);
        graphics.drawLine(width, 0, width, height);
    }

}
