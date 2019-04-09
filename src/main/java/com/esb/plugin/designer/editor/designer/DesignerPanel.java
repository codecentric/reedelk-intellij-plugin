package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.DesignerPanelDropListener;
import com.esb.plugin.designer.editor.GraphChangeListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.layout.FlowGraphLayout;
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

    private boolean updated;
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

        if (updated) {
            // This should be done only if updated!!
            FlowGraphLayout.compute(graph, g2);
            adjustWindowSize();
            updated = false;
        }

        // Draw the graph
        graph.breadthFirstTraversal(graph.root(),
                node -> node.draw(graph, g2, this));
    }

    @Override
    public void updated(FlowGraph updatedGraph) {
        checkState(updatedGraph != null, "Updated Graph Was null");
        SwingUtilities.invokeLater(() -> {
            graph = updatedGraph;
            updated = true;
            invalidate();
            repaint();
        });
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (dragging) {
            if (selected != null) {
                selected.drag(event.getX() - offsetx, event.getY() - offsety);
                repaint();
            }
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
            resetSelected();

            selected = drawableWithinCoordinates.get();

            offsetx = event.getX() - selected.x();
            offsety = event.getY() - selected.y();

            selected.selected();
            selected.dragging();
            selected.drag(event.getX() - offsetx, event.getY() - offsety);

            dragging = true;

        } else {
            // If no drawable is selected, then we reset it.
            resetSelected();
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;

            if (selected != null) {
                int x = e.getX();
                int y = e.getY();

                selected.drag(x, y);
                selected.release();
                designerPanelDropListener.drop(x, y, selected);
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

    private void resetSelected() {
        if (selected != null) {
            selected.unselected();
            selected = null;
        }
    }
}
