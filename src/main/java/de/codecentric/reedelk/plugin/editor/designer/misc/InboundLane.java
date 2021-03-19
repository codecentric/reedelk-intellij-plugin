package de.codecentric.reedelk.plugin.editor.designer.misc;

import de.codecentric.reedelk.plugin.editor.designer.AbstractGraphNode;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import com.intellij.ui.components.JBPanel;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Fonts;
import de.codecentric.reedelk.plugin.commons.Half;
import de.codecentric.reedelk.plugin.commons.Images;
import de.codecentric.reedelk.plugin.editor.designer.icon.Icon;
import de.codecentric.reedelk.plugin.editor.designer.text.AbstractText;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Collections;
import java.util.List;

public class InboundLane {

    private static final String INBOUND_STRING = "Event";
    private final Stroke dashed = new BasicStroke(0.7f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);

    private final InboundComponent inboundComponent;
    private final int topPadding;
    private final JBPanel parent;

    public InboundLane(int topPadding, JBPanel parent) {
        this.inboundComponent = new InboundComponent();
        this.topPadding = topPadding;
        this.parent = parent;
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
                AbstractGraphNode.HALF_NODE_WIDTH - Icon.Dimension.HALF_ICON_WIDTH,
                topPadding + Icon.Dimension.ICON_HEIGHT + Icon.Dimension.TOP_PADDING - Icon.Dimension.ICON_WIDTH, observer);
    }

    private void drawVerticalBar(Graphics2D graphics, int width) {
        graphics.setColor(Colors.DESIGNER_INBOUND_LANE_VERTICAL_BAR);
        graphics.setStroke(dashed);
        graphics.drawLine(width, 0, width, parent.getHeight());
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
