package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.DesignerPanelDropListener;
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
    private Drawable selected;

    private int offsetx;
    private int offsety;

    private boolean dragging;
    private DesignerPanelDropListener designerPanelDropListener;

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
            selected.drag(event.getX() - offsetx, event.getY() - offsety);
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
        if (drawableWithinCoordinates.isPresent()) {
            if (selected != null) {
                selected.unselected();
                selected = null;
            }
            selected = drawableWithinCoordinates.get();
            selected.selected();
            dragging = true;
            selected.dragging();

            offsetx = event.getX() - selected.x();
            offsety = event.getY() - selected.y();

            selected.drag(event.getX() - offsetx, event.getY() - offsety);

        } else {
            if (selected != null) {
                selected.unselected();
                selected = null;
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            int x = e.getX();
            int y = e.getY();
            dragging = false;

            if (selected != null) {
                selected.drag(x, y);
                selected.release();
                designerPanelDropListener.drop(x, y, selected);
            } else {
                repaint();
            }
        }
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

    /**
     * We might need to adapt window size, since the new graph might have
     * grown beyond the X (or Y) axis.
     */
    private void adjustWindowSize() {
        int maxX = graph.nodes().stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = graph.nodes().stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + Tile.WIDTH;
        int newSizeY = maxY + Tile.HEIGHT;
        setSize(new Dimension(newSizeX, newSizeY));
        setPreferredSize(new Dimension(newSizeX, newSizeY));
    }

    /**
     * TODO: Graph should never be null.
     */
    private Optional<Drawable> getDrawableWithinCoordinates(int x, int y) {
        return graph == null ? Optional.empty() :
                graph.nodes()
                        .stream()
                        .filter(drawable -> drawable.contains(this, x, y))
                        .findFirst();
    }

    public void setDesignerPanelDropListener(DesignerPanelDropListener dropListener) {
        this.designerPanelDropListener = dropListener;
    }
}
