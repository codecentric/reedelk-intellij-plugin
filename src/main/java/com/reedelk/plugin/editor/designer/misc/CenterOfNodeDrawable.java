package com.reedelk.plugin.editor.designer.misc;

import com.reedelk.plugin.commons.DebugControls;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;
import java.util.function.Consumer;

public class CenterOfNodeDrawable {

    private final FlowSnapshot snapshot;

    public CenterOfNodeDrawable(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public void draw(Graphics2D g2) {
        if (DebugControls.Designer.SHOW_BOX) {
            draw(node -> {
                int width = node.width(g2);
                int height = node.height(g2);
                int x = node.x() - Half.of(width);
                int y = node.y() - node.topHalfHeight(g2);
                g2.drawRect(x, y, width, height);
            });
        }

        if (DebugControls.Designer.SHOW_CENTER) {
            draw(node ->
                    g2.drawOval(node.x() - 5, node.y() - 5, 10, 10));
        }

        if (DebugControls.Designer.SHOW_COORDS) {
            draw(node -> {
                String coords = String.format("[x:%d,y:%d]", node.x(), node.y());
                double textHeight = g2.getFontMetrics().getStringBounds(coords, g2).getHeight();
                int x = node.x() - Half.of(node.width(g2));
                int y = node.y() - node.topHalfHeight(g2);
                g2.drawString(coords, x + 3, y + Math.round(textHeight) - 3);
            });
        }

        if (DebugControls.Designer.SHOW_HEIGHTS) {
            draw(node -> {
                String height = String.format("[Half T:%d, B:%d]", node.topHalfHeight(g2), node.bottomHalfHeight(g2));
                double textHeight = g2.getFontMetrics().getStringBounds(height, g2).getHeight();
                int x = node.x() - Half.of(node.width(g2));
                int y = node.y() - node.topHalfHeight(g2);
                g2.drawString(height, x + 3, y + Math.round(textHeight) * 2 - 3);
            });
        }
    }

    public void draw(Consumer<GraphNode> drawFunction) {
        snapshot.getGraphOrThrowIfAbsent().breadthFirstTraversal(drawFunction);
    }
}
