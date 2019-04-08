package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.commons.ESBIcons;
import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.List;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public abstract class AbstractDrawable implements Drawable {

    private static final int INNER_PADDING = 4;
    private final Stroke DOTTED_STROKE = new BasicStroke(0.7f, CAP_ROUND, JOIN_ROUND, 0, new float[]{3}, 0);
    private final Image image;
    private final Component component;

    // x and y are the center position os this Drawable.
    private int x;
    private int y;
    private int draggedX;
    private int draggedY;

    private boolean selected;
    private boolean dragging;

    public AbstractDrawable(Component component) {
        this.component = component;
        this.image = ESBIcons.forComponentAsImage(component.getName());
    }

    private static int drawText(Graphics graphics, String stringToDraw, int centerX, int topY) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(stringToDraw, graphics);
        int stringWidth = (int) stringBounds.getWidth();
        int stringHeight = (int) stringBounds.getHeight();
        int startX = centerX - Math.floorDiv(stringWidth, 2);
        int startY = topY + stringHeight;
        graphics.drawString(stringToDraw, startX, startY);
        return stringHeight;
    }

    @Override
    public Component component() {
        return component;
    }

    @Override
    public String displayName() {
        String[] segments = component.getName().split("\\.");
        return segments[segments.length - 1];
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int height() {
        return Tile.HEIGHT;
    }

    @Override
    public int width() {
        return Tile.WIDTH;
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        int halfImageWidth = Math.floorDiv(image.getWidth(observer), 2);
        int halfImageHeight = Math.floorDiv(image.getHeight(observer), 2);
        boolean containsOnXAxis =
                x >= this.x - halfImageWidth &&
                        x <= this.x + halfImageWidth;

        boolean containsOnYAxis =
                y >= this.y - halfImageHeight &&
                        y <= this.y + halfImageHeight;

        return containsOnXAxis && containsOnYAxis;
    }

    @Override
    public void selected() {
        this.selected = true;
    }

    @Override
    public void unselected() {
        this.selected = false;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        drawIcon(graphics, observer);
        drawComponentNameAndDescription(graphics, observer);
        drawArrows(graph, graphics, observer);

        if (selected) {
            drawSelectedItemBox(graphics);
        }

        if (dragging) {
            drawDraggedElement(graphics, observer);
        }
    }

    @Override
    public void drag(int x, int y) {
        this.draggedX = x;
        this.draggedY = y;
    }

    @Override
    public void dragging() {
        this.dragging = true;
    }

    @Override
    public void release() {
        this.dragging = false;
    }

    protected void drawComponentNameAndDescription(Graphics graphics, ImageObserver observer) {
        int textCenterX = x();
        int textTopY = y() + Math.floorDiv(image.getHeight(observer), 2);

        graphics.setColor(JBColor.GRAY);
        textTopY += drawText(graphics, displayName(), textCenterX, textTopY);

        graphics.setColor(JBColor.LIGHT_GRAY);
        drawText(graphics, "A Description", textCenterX, textTopY);
    }

    protected void drawIcon(Graphics2D graphics, ImageObserver observer) {
        int imageX = x() - Math.floorDiv(image.getWidth(observer), 2);
        int imageY = y() - Math.floorDiv(image.getHeight(observer), 2);
        graphics.drawImage(image, imageX, imageY, observer);
    }

    private void drawDraggedElement(Graphics2D graphics, ImageObserver observer) {
        int imageX = draggedX - Math.floorDiv(image.getWidth(observer), 2);
        int imageY = draggedY - Math.floorDiv(image.getHeight(observer), 2);
        graphics.drawImage(image, imageX, imageY, observer);

        int textCenterX = draggedX;
        int textTopY = draggedY + Math.floorDiv(image.getHeight(observer), 2);

        graphics.setColor(JBColor.GRAY);
        textTopY += drawText(graphics, displayName(), textCenterX, textTopY);

        graphics.setColor(JBColor.LIGHT_GRAY);
        drawText(graphics, "A Description", textCenterX, textTopY);
    }

    private void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        List<Drawable> successors = graph.successors(this);
        for (Drawable successor : successors) {
            Arrow arrow = new Arrow(this, successor);
            arrow.draw(graph, graphics, observer);
        }
    }

    private void drawSelectedItemBox(Graphics2D graphics) {
        graphics.setStroke(DOTTED_STROKE);
        graphics.setColor(JBColor.lightGray);

        int halfWidth = Math.floorDiv(width(), 2);
        int halfHeight = Math.floorDiv(height(), 2);
        int x1 = x() - halfWidth + INNER_PADDING;
        int y1 = y() - halfHeight + INNER_PADDING;
        int x2 = x() + halfWidth - INNER_PADDING;
        int y2 = y() - halfHeight + INNER_PADDING;
        int x3 = x() - halfWidth + INNER_PADDING;
        int y3 = y() + halfHeight - INNER_PADDING;
        int x4 = x() + halfWidth - INNER_PADDING;
        int y4 = y() + halfHeight - INNER_PADDING;

        graphics.drawLine(x1, y1, x2, y2);
        graphics.drawLine(x2, y2, x4, y4);
        graphics.drawLine(x4, y4, x3, y3);
        graphics.drawLine(x3, y3, x1, y1);
    }
}
