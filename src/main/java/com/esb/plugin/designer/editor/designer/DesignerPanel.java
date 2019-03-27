package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.editor.common.FlowDataStructureListener;
import com.esb.plugin.designer.editor.common.Tile;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.handler.Drawable;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBDimension;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, FlowDataStructureListener {

    private final JBColor BACKGROUND_COLOR = JBColor.WHITE;
    private final Color GRID_COLOR = new JBColor(new Color(226, 226, 236, 255), new Color(226, 226, 236, 255));

    private final int PREFERRED_WIDTH = 700;
    private final int PREFERRED_HEIGHT = 400;

    private final FlowGraph graph;

    private boolean dragging;
    private Drawable selected;
    private int offsetx;
    private int offsety;

    public DesignerPanel(FlowGraph graph) {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new JBDimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        //setDropTarget(new DesignerPanelDropTarget(flowDataStructure, this));
        // addMouseListener(this);
        //addMouseMotionListener(this);

        this.graph = graph;
        //this.flowDataStructure.setListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        //flowDataStructure.draw(g);
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
            } else if (selectedPosition.x + selected.width(this) > getWidth()) {
                selectedPosition.x = getWidth() - selected.width(this);
            }
            if (selectedPosition.y < 0) {
                selectedPosition.y = 0;
            } else if (selectedPosition.y + selected.height(this) > getHeight()) {
                selectedPosition.y = getHeight() - selected.height(this);
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

        //  computeSnapToGridCoordinates(selected, x, y);

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
        // TODO: Implement this
        //return drawableList.stream().filter(drawable -> drawable.contains(new Point(x, y))).findFirst();
        return Optional.empty();
    }

    public void add(Drawable component) {
        int x = component.getPosition().x;
        int y = component.getPosition().y;

        //  computeSnapToGridCoordinates(component, x, y);

        // TODO: Implement this
        //drawableList.add(component);
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

    @Override
    public void onChange() {
        repaint();
    }
}
