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

    private static final JBColor BACKGROUND_COLOR = JBColor.WHITE;

    private FlowGraph graph;

    private boolean dragging;
    private Drawable selected;
    private int offsetx;
    private int offsety;

    public DesignerPanel() {
        setBackground(BACKGROUND_COLOR);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (graph == null) return;

        // Set Antialiasing
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        // Draw the graph
        graph.breadthFirstTraversal(graph.root(),
                node -> node.draw(graph, g2, this));
    }

    @Override
    public void updated(FlowGraph updatedGraph) {
        checkState(updatedGraph != null, "Updated Graph Was null");

        SwingUtilities.invokeLater(() -> {
            graph = updatedGraph;
            adjustWindowSize();
            invalidate();
            repaint();
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
        Optional<Drawable> drawableWithinCoordinates = getDrawableWithinCoordinates(x, y);
        if (drawableWithinCoordinates.isPresent()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        Optional<Drawable> drawableWithinCoordinates = getDrawableWithinCoordinates(x, y);
        drawableWithinCoordinates.ifPresent(target -> {
            if (selected != null) {
                selected.unselected();
                selected = null;
            }
            selected = target;
            target.selected();
            dragging = true;
            offsetx = event.getX() - selected.x();
            offsety = event.getY() - selected.y();
        });

        if (!drawableWithinCoordinates.isPresent()) {
            if (selected != null) {
                selected.unselected();
                selected = null;
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!dragging) return;

        // Compute snap to grid, set new position, redraw and reset dragging state

        int x = e.getX();
        int y = e.getY();

        //  computeSnapToGridCoordinates(selected, x, y);


        // selected.unselected();
        //selected = null;
        //dragging = false;

        //  repaint();
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

    private void adjustWindowSize() {
        // We might need to adapt window size, since the new graph might
        // have grown on the X (or Y) axis.
        int maxX = graph.nodes().stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = graph.nodes().stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + Tile.WIDTH;
        int newSizeY = maxY + Tile.HEIGHT;
        setSize(new Dimension(newSizeX, newSizeY));
        setPreferredSize(new Dimension(newSizeX, newSizeY));
    }

    private Optional<Drawable> getDrawableWithinCoordinates(int x, int y) {
        // TODO: Remove all these checks if graph is null! Use
        // TODO: something more object oriented!! Like an empty graph!
        if (graph == null) return Optional.empty();
        return graph
                .nodes()
                .stream()
                .filter(drawable -> drawable.contains(this, x, y))
                .findFirst();
    }

}
