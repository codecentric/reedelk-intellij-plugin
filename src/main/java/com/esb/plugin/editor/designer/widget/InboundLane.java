package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Fonts;
import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.layout.utils.ComputeMaxHeight;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class InboundLane {

    private static final int DEFAULT_INBOUND_LANE_HEIGHT = 170;

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

    public void draw(Graphics2D graphics) {
        int width = AbstractGraphNode.WIDTH;
        drawVerticalBar(graphics, width);
        inboundComponent.setPosition(Half.of(width), Half.of(topPadding));
        inboundComponent.draw(graphics);
    }

    class InboundComponent extends AbstractText {

        private InboundComponent() {
            super(Fonts.Component.INBOUND, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        }

        @Override
        protected Color getColor() {
            return JBColor.GRAY;
        }

        @Override
        protected List<String> getText() {
            return Collections.singletonList(INBOUND_STRING);
        }
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
