package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.commons.Fonts;
import com.esb.plugin.commons.Half;
import com.esb.plugin.commons.Images;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.layout.ComputeMaxHeight;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Collections;
import java.util.List;

public class InboundLane {

    private static final int MIN_INBOUND_LANE_HEIGHT = 170;

    private final Stroke dashed = new BasicStroke(0.7f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
    private final String INBOUND_STRING = "Event";

    private final InboundComponent inboundComponent;
    private final FlowSnapshot snapshot;
    private final int topPadding;

    public InboundLane(FlowSnapshot snapshot, int topPadding) {
        this.inboundComponent = new InboundComponent();
        this.topPadding = topPadding;
        this.snapshot = snapshot;
    }

    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        int width = AbstractGraphNode.NODE_WIDTH;
        drawVerticalBar(graphics, width);
        inboundComponent.setPosition(Half.of(width), Half.of(topPadding));
        inboundComponent.draw(graphics);

        if (graph.isEmpty()) {
            drawInboundComponentIcon(graphics, observer);
        }
    }

    private void drawInboundComponentIcon(Graphics2D graphics, ImageObserver observer) {
        Image inboundPlaceholderImage = Images.Component.InboundPlaceholderIcon;
        graphics.drawImage(inboundPlaceholderImage,
                Half.of(AbstractGraphNode.NODE_WIDTH) - Icon.Dimension.HALF_ICON_WIDTH,
                topPadding + Icon.Dimension.ICON_HEIGHT + Icon.Dimension.TOP_PADDING - Icon.Dimension.ICON_WIDTH, observer);
    }

    private void drawVerticalBar(Graphics2D graphics, int width) {
        int height = topPadding;
        FlowGraph graph = snapshot.getGraph();
        if (graph.isEmpty()) {
            height += MIN_INBOUND_LANE_HEIGHT;
        } else {
            GraphNode root = graph.root();
            int maxHeight = ComputeMaxHeight.of(graph, graphics, root);
            height += maxHeight > MIN_INBOUND_LANE_HEIGHT ?
                    maxHeight :
                    MIN_INBOUND_LANE_HEIGHT;
        }
        graphics.setColor(Colors.DESIGNER_INBOUND_LANE_VERTICAL_BAR);
        graphics.setStroke(dashed);
        graphics.drawLine(width, 0, width, height);
    }

    class InboundComponent extends AbstractText {

        private InboundComponent() {
            super(Fonts.Component.INBOUND, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        }

        @Override
        protected Color getColor() {
            return Colors.TEXT_INBOUND_LANE;
        }

        @Override
        protected List<String> getText() {
            return Collections.singletonList(INBOUND_STRING);
        }
    }
}
