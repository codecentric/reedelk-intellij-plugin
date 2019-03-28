package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBDimension;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener {

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
        //addMouseListener(this);
        //addMouseMotionListener(this);

        this.graph = graph;
        this.graph.computePositions();
        //this.flowDataStructure.setListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graph.breadthFirstTraversal(graph.root(),
                node -> node.draw(graphics, this));
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (!dragging) return;

        if (selected != null) {


            selected.setPosition(event.getX() - offsetx, event.getY() - offsety);  // Get new position of rect.


            /* Clamp (x,y) so that dragging does not goes beyond frame border */
/**
 if (selected.x < 0) {
 selected.x = 0;
            } else if (selectedPosition.x + selected.width(this) > getWidth()) {
                selectedPosition.x = getWidth() - selected.width(this);
            }
            if (selectedPosition.y < 0) {
                selectedPosition.y = 0;
            } else if (selectedPosition.y + selected.height(this) > getHeight()) {
                selectedPosition.y = getHeight() - selected.height(this);
 }*/

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

        /**
         Optional<Drawable> first = getDrawableWithin(x, y);
         first.ifPresent(drawable -> {
         selected = drawable;
         dragging = true;
         offsetx = event.getX() - selected.getPosition().x;
         offsety = event.getY() - selected.getPosition().y;
         });
         */
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
        /**
        int x = component.getPosition().x;
        int y = component.getPosition().y;
         */

        //  computeSnapToGridCoordinates(component, x, y);

        // TODO: Implement this
        //drawableList.add(component);
    }

    private void computeSnapToGridCoordinates(Drawable drawable, int x, int y) {
        // Get the closest X and Y coordinate to the center of a Tile
        /**
         int snapX = Math.floorDiv(x, Tile.INSTANCE.width) * Tile.INSTANCE.width;
         int snapY = Math.floorDiv(y, Tile.INSTANCE.height) * Tile.INSTANCE.height;
         drawable.getPosition().x = snapX;
         drawable.getPosition().y = snapY;
         */
    }

}
