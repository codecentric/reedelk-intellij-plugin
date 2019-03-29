package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.GraphChangeListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, GraphChangeListener {

    private final JBColor BACKGROUND_COLOR = JBColor.WHITE;

    private FlowGraph graph;

    private boolean dragging;
    private Drawable selected;
    private int offsetx;
    private int offsety;

    public DesignerPanel() {
        setBackground(BACKGROUND_COLOR);
        addMouseMotionListener(this);
        //addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (graph != null) {
            paintGraph(graphics, graph);
        }
    }

    @Override
    public void updated(FlowGraph updatedGraph) {
        checkState(updatedGraph != null, "Updated Graph Was null");

        SwingUtilities.invokeLater(() -> {
            graph = updatedGraph;
            adjustWindowSize();
            invalidate();
        });
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

    // Changes the cursor when mouse goes over (or out) a Drawable Node in the flow.
    @Override
    public void mouseMoved(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        Optional<Drawable> first = getDrawableContaining(x, y);
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

    private void adjustWindowSize() {
        // We might need to adapt window size, since the new graph might
        // have grown on the X (or Y) axis.
        int maxX = graph.nodes().stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = graph.nodes().stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + Math.floorDiv(Tile.WIDTH, 2);
        int newSizeY = maxY + Tile.HEIGHT;
        setSize(new Dimension(newSizeX, newSizeY));
        setPreferredSize(new Dimension(newSizeX, newSizeY));
    }

    private Optional<Drawable> getDrawableContaining(int x, int y) {
        // TODO: Remove all these checks if graph is null! Use
        // TODO: something more object oriented!! Like an empty graph!
        if (graph == null) return Optional.empty();
        return graph
                .nodes()
                .stream()
                .filter(drawable -> drawable.contains(x, y))
                .findFirst();
    }

    private void paintGraph(Graphics graphics, FlowGraph graph) {
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graph.breadthFirstTraversal(
                graph.root(),
                node -> node.draw(graphics, this));
    }

}
