package com.reedelk.plugin.editor.designer.text;

import com.reedelk.plugin.commons.Half;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public abstract class AbstractText {

    private static final String PROTOTYPE_TEXT = "XXXXXX";

    private final HorizontalAlignment horizontalAlignment;
    private final VerticalAlignment verticalAlignment;
    private final Font font;

    private boolean selected;
    private int x;
    private int y;

    public AbstractText(Font font, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
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
        // We must set the font to the graphics object before measuring the string
        // metrics, so that the metrics are correctly computed according to the font
        // style and font size.
        graphics.setFont(font);

        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(PROTOTYPE_TEXT, graphics);
        List<String> textAsLines = getText();

        // Even if the text is empty, we always keep the space for one line.
        int numberOfTextLines = textAsLines.isEmpty() ? 1 : textAsLines.size();
        return (int) stringBounds.getHeight() * numberOfTextLines;
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
