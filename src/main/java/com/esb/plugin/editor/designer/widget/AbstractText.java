package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public abstract class AbstractText {

    private final HorizontalAlignment horizontalAlignment;
    private final VerticalAlignment verticalAlignment;
    private final Font font;

    private boolean selected;
    private int x;
    private int y;

    AbstractText(Font font, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.font = font;
    }

    public void draw(Graphics2D graphics) {
        graphics.setColor(selected ? getSelectedColor() : getColor());
        graphics.setFont(font);

        int totalLines = getText().size();
        int count = 0;
        for (String line : getText()) {
            Rectangle2D bounds = graphics.getFontMetrics().getStringBounds(line, graphics);

            int width = (int) bounds.getWidth();
            int halfWidth = Half.of(width);
            int height = (int) bounds.getHeight();

            int startX = 0;
            int startY = 0;

            if (HorizontalAlignment.CENTER.equals(horizontalAlignment)) {
                startX = x - halfWidth;
            } else if (HorizontalAlignment.RIGHT.equals(horizontalAlignment)) {
                startX = x;
            }

            if (VerticalAlignment.CENTER.equals(verticalAlignment)) {
                int totalHeight = (int) bounds.getHeight() * totalLines;
                int halfTotalHeight = Half.of(totalHeight);
                startY = y - halfTotalHeight + (height * count + 1);

            } else if (VerticalAlignment.BELOW.equals(verticalAlignment)) {
                startY = y + height * (count + 1);
            }

            graphics.drawString(line, startX, startY);

            count++;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int height(Graphics2D graphics) {
        int height = 0;
        if (getText() != null && !getText().isEmpty()) {
            Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(getText().get(0), graphics);
            List<String> textAsLines = getText();
            height = (int) stringBounds.getHeight() * textAsLines.size();
        }
        return height;
    }

    public void selected() {
        this.selected = true;
    }

    public void unselected() {
        this.selected = false;
    }

    protected abstract Color getColor();

    protected Color getSelectedColor() {
        return getColor();
    }

    protected abstract List<String> getText();

    public enum HorizontalAlignment {
        RIGHT,
        CENTER
    }

    public enum VerticalAlignment {
        BELOW,
        CENTER
    }
}
