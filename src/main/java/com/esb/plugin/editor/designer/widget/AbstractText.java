package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractText {

    private static final Pattern REGEX = Pattern.compile(".{1,18}(?:\\s|$)", Pattern.DOTALL);
    private final Font font;

    private int x;
    private int y;

    private boolean selected;

    protected AbstractText(Font font) {
        this.font = font;
    }

    public void draw(Graphics2D graphics) {
        graphics.setColor(selected ? getSelectedColor() : getColor());
        graphics.setFont(font);

        int count = 0;
        for (String line : getTextAsLines()) {
            Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(line, graphics);
            int halfWidth = Half.of((int) stringBounds.getWidth());
            int halfHeight = Half.of((int) stringBounds.getHeight());
            int startX = x - halfWidth;
            int startY = y + halfHeight + ((int) stringBounds.getHeight() * count);
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
        if (getText() != null) {
            Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(getText(), graphics);
            List<String> textAsLines = getTextAsLines();
            height = (int) stringBounds.getHeight() * textAsLines.size();
        }
        return height;
    }

    public int width(Graphics2D graphics) {
        int width = 0;
        if (getText() != null) {
            Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(getText(), graphics);
            width = (int) stringBounds.getWidth();
        }
        return width;
    }

    protected abstract String getText();

    protected abstract Color getColor();

    protected abstract Color getSelectedColor();

    private List<String> getTextAsLines() {
        List<String> matchList = new ArrayList<>();

        if (getText() == null) return matchList;

        Matcher regexMatcher = REGEX.matcher(getText());
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList;
    }

    public void selected() {
        this.selected = true;
    }

    public void unselected() {
        this.selected = false;
    }
}
