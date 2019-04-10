package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.DesignerPanelDropListener;
import com.esb.plugin.designer.editor.GraphChangeListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphLayout;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.decorators.NothingSelectedDrawable;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;


public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, GraphChangeListener {

    private final Drawable NOTHING_SELECTED = new NothingSelectedDrawable();

    private static final JBColor BACKGROUND_COLOR = JBColor.WHITE;

    private FlowGraph graph;
    private Drawable selected = NOTHING_SELECTED;

    private int offsetx;
    private int offsety;

    private boolean updated;
    private boolean dragging;
    private DesignerPanelDropListener designerPanelDropListener;


    public DesignerPanel() {
        setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (graph == null) return;

        // Set Antialiasing
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        // We compute again the graph layout if and only if it was updated.
        if (updated) {
            FlowGraphLayout.compute(graph, g2);
            adjustWindowSize();
            updated = false;
        }

        Collection<Drawable> graphNodes = graph.nodes();

        // Draw each node of the graph
        graphNodes.forEach(drawable -> {
            // We skip the current selected drawable,
            // since it must be drawn on to of all the other ones LAST (see below).
            if (!drawable.isSelected()) {
                drawable.draw(graph, g2, DesignerPanel.this);
            }
        });

        // The selected drawable must be drawn LAST so
        // that it is on top of all the other drawables.
        graphNodes
                .stream()
                .filter(Drawable::isSelected)
                .findFirst()
                .ifPresent(drawable -> drawable.draw(graph, g2, DesignerPanel.this));
    }

    @Override
    public void updated(FlowGraph updatedGraph) {
        checkState(updatedGraph != null, "Updated graph must not be null");
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
            selected.drag(event.getX() - offsetx, event.getY() - offsety);
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
            // Unselect the previous one
            selected.unselected();
            selected = drawableWithinCoordinates.get();

            offsetx = event.getX() - selected.x();
            offsety = event.getY() - selected.y();

            selected.selected();
            selected.dragging();
            selected.drag(event.getX() - offsetx, event.getY() - offsety);

            dragging = true;

        } else {
            // If no drawable is selected, then we reset it.
            selected.unselected();
            selected = NOTHING_SELECTED;
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
            int dragX = e.getX();
            int dragY = e.getY();
            selected.drag(dragX, dragY);
            selected.release();

            if (Math.abs(dragX - offsetx - selected.x()) > 15 ||
                    Math.abs(dragY - offsety - selected.y()) > 15) {
                if (!(selected instanceof NothingSelectedDrawable)) {
                    designerPanelDropListener.drop(dragX, dragY, selected);
                }
            } else {
                repaint();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // nothing to do
    }

    /**
     * We might need to adapt window size, since the new graph might have grown beyond the X (or Y) axis.
     */
    private void adjustWindowSize() {
        int maxX = graph.nodes().stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = graph.nodes().stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + Tile.WIDTH;
        int newSizeY = maxY + Tile.HEIGHT;
        setSize(new Dimension(newSizeX, newSizeY));
        setPreferredSize(new Dimension(newSizeX, newSizeY));
    }

    private Optional<Drawable> getDrawableWithinCoordinates(int x, int y) {
        //TODO: Graph should never be null.
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
