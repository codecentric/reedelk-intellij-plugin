package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractText implements Widget {

    private static final Pattern REGEX = Pattern.compile(".{1,16}(?:\\s|$)", Pattern.DOTALL);
    private final Font font;

    private int x;
    private int y;

    protected AbstractText(Font font) {
        this.font = font;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setColor(getColor());
        graphics.setFont(font);

        int count = 0;
        for (String line : getTextAsLines()) {
            Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(line, graphics);
            int halfWidth = Math.floorDiv((int) stringBounds.getWidth(), 2);
            int halfHeight = Math.floorDiv((int) stringBounds.getHeight(), 2);
            int startX = x - halfWidth;
            int startY = y + halfHeight + ((int) stringBounds.getHeight() * count);
            graphics.drawString(line, startX, startY);
            count++;
        }
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int height(Graphics2D graphics) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(getText(), graphics);
        return (int) stringBounds.getHeight();
    }

    @Override
    public int width(Graphics2D graphics) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(getText(), graphics);
        return (int) stringBounds.getWidth();
    }

    protected abstract String getText();

    protected abstract Color getColor();

    private List<String> getTextAsLines() {
        java.util.List<String> matchList = new ArrayList<>();
        Matcher regexMatcher = REGEX.matcher(getText());
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList;
    }
}
