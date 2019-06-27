package com.esb.plugin.editor.designer.widget;

import com.esb.internal.commons.StringUtils;
import com.esb.plugin.commons.Half;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowSnapshot;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class FlowMetadata {

    private final int topPadding;
    private final FlowSnapshot snapshot;

    public FlowMetadata(FlowSnapshot snapshot, int topPadding) {
        this.topPadding = topPadding;
        this.snapshot = snapshot;
    }

    public void draw(Graphics2D graphics) {
        int width = AbstractGraphNode.WIDTH;

        Font font = graphics.getFont().deriveFont(20f);
        graphics.setFont(font);

        String title = snapshot.getGraph().title();
        if (StringUtils.isNotBlank(title)) {
            graphics.drawString(title, width + 20, Half.of(topPadding));
        }

        font = graphics.getFont().deriveFont(13f);
        graphics.setFont(font);
        String description = snapshot.getGraph().description();

        if (StringUtils.isNotBlank(description)) {
            Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(description, graphics);
            graphics.drawString(description, width + 20, Half.of(topPadding) + (int) stringBounds.getHeight() + 5);
        }
    }
}
