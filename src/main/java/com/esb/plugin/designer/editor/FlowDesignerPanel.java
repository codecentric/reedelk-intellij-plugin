package com.esb.plugin.designer.editor;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlowDesignerPanel extends JPanel implements MouseMotionListener, MouseListener {


    private static final Color GRID_COLOR = new Color(226, 226, 236, 255);
    private List<Drawable> drawableList = new ArrayList<>();
    private boolean dragging;
    private Drawable selected;
    private int offsetx;
    private int offsety;

    public FlowDesignerPanel() {
        setSize(new Dimension(700, 400));
        setDropTarget(new DrawingPanelDropTarget(this));
        setBackground(JBColor.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawableList.forEach(drawable -> drawable.draw(g));
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (!dragging) return;

        if (selected != null) {
            Point selectedPosition = selected.getPosition();

            selectedPosition.x = event.getX() - offsetx;  // Get new position of rect.
            selectedPosition.y = event.getY() - offsety;

            /* Clamp (x,y) so that dragging does not goes beyond frame border */

            if (selectedPosition.x < 0) {
                selectedPosition.x = 0;
            } else if (selectedPosition.x + selected.width() > getWidth()) {
                selectedPosition.x = getWidth() - selected.width();
            }
            if (selectedPosition.y < 0) {
                selectedPosition.y = 0;
            } else if (selectedPosition.y + selected.height() > getHeight()) {
                selectedPosition.y = getHeight() - selected.height();
            }

            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        Optional<Drawable> first = getDrawableWithin(x, y);
        if (first.isPresent()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        Optional<Drawable> first = getDrawableWithin(x, y);
        first.ifPresent(drawable -> {
            selected = drawable;
            dragging = true;
            offsetx = event.getX() - selected.getPosition().x;
            offsety = event.getY() - selected.getPosition().y;
        });
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!dragging) return;

        // Compute snap to grid, set new position, redraw and reset dragging state

        int x = e.getX();
        int y = e.getY();

        computeSnapToGridCoordinates(selected, x, y);

        selected = null;
        dragging = false;

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private Optional<Drawable> getDrawableWithin(int x, int y) {
        return drawableList.stream().filter(drawable -> drawable.contains(new Point(x, y))).findFirst();
    }

    public void add(Drawable component) {
        int x = component.getPosition().x;
        int y = component.getPosition().y;

        computeSnapToGridCoordinates(component, x, y);

        drawableList.add(component);
    }

    private void drawGrid(Graphics graphics) {

        graphics.setColor(GRID_COLOR);

        // Draw Columns
        int columns = Math.floorDiv(getWidth(), Tile.INSTANCE.width);
        for (int i = 0; i < columns; i++) {
            int startX = (i * Tile.INSTANCE.width) + Tile.INSTANCE.width;
            int startY = 0;
            int stopX = (i * Tile.INSTANCE.width) + Tile.INSTANCE.width;
            int stopY = getHeight();
            graphics.drawLine(startX, startY, stopX, stopY);
        }

        // Draw Rows
        int rows = Math.floorDiv(getHeight(), Tile.INSTANCE.height);
        for (int i = 0; i < rows; i++) {
            int startX = 0;
            int startY = (i * Tile.INSTANCE.height) + Tile.INSTANCE.height;
            int stopX = getWidth();
            int stopY = (i * Tile.INSTANCE.height) + Tile.INSTANCE.height;
            graphics.drawLine(startX, startY, stopX, stopY);
        }

        graphics.setColor(JBColor.WHITE);
    }

    private void computeSnapToGridCoordinates(Drawable drawable, int x, int y) {
        // Get the closest X and Y coordinate to the center of a Tile
        int snapX = Math.floorDiv(x, Tile.INSTANCE.width) * Tile.INSTANCE.width;
        int snapY = Math.floorDiv(y, Tile.INSTANCE.height) * Tile.INSTANCE.height;
        drawable.getPosition().x = snapX;
        drawable.getPosition().y = snapY;
    }
}
