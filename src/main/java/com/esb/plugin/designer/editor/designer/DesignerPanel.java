package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.GraphChangeListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            paintConnections(graphics, graph);
            paintScopes(graphics, graph);
        }
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
        int newSizeX = maxX + Tile.WIDTH;
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

    private void paintConnections(Graphics graphics, FlowGraph graph) {
        paintConnection(graphics, graph, graph.root());
    }

    private void paintConnection(Graphics graphics, FlowGraph graph, Drawable root) {
        List<Drawable> successors = graph.successors(root);
        for (Drawable successor : successors) {
            graphics.setColor(JBColor.lightGray);
            Arrow.draw((Graphics2D) graphics,
                    new Point2D.Double(root.x() + Math.floorDiv(Tile.WIDTH, 2) - 15, root.y()),
                    new Point2D.Double(successor.x() - Math.floorDiv(Tile.WIDTH, 2) + 15, successor.y()),
                    10);
            paintConnection(graphics, graph, successor);
        }
    }

    private void paintScopes(Graphics graphics, FlowGraph graph) {
        List<ScopedDrawable> scopedDrawables = graph.nodes().stream()
                .filter(drawable -> drawable instanceof ScopedDrawable)
                .map(drawable -> (ScopedDrawable) drawable)
                .collect(Collectors.toList());

        for (ScopedDrawable scopedDrawable : scopedDrawables) {
            paintScope(graphics, scopedDrawable);
        }
    }

    private <T extends ScopedDrawable> void paintScope(Graphics graphics, T scopedDrawable) {
        Collection<Drawable> drawables = scopedDrawable.listDrawables();
        if (drawables.isEmpty()) {
            return;
        }

        List<Drawable> drawables1 = new ArrayList<>(drawables);
        drawables1.add(scopedDrawable);
        int maxX = drawables1.stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = drawables1.stream().mapToInt(Drawable::y).max().getAsInt();
        int minX = drawables1.stream().mapToInt(Drawable::x).min().getAsInt();
        int minY = drawables1.stream().mapToInt(Drawable::y).min().getAsInt();


        // Draw line between :
        int line1X = minX - Tile.HALF_WIDTH;
        int line1Y = minY - Tile.HALF_HEIGHT;

        int line2X = maxX + Tile.HALF_WIDTH;
        int line2Y = minY - Tile.HALF_HEIGHT;

        int line3X = maxX + Tile.HALF_WIDTH;
        int line3Y = maxY + Tile.HALF_HEIGHT;

        int line4X = minX - Tile.HALF_WIDTH;
        int line4Y = maxY + Tile.HALF_HEIGHT;

        graphics.drawLine(line1X, line1Y, line2X, line2Y);
        graphics.drawLine(line2X, line2Y, line3X, line3Y);
        graphics.drawLine(line3X, line3Y, line4X, line4Y);
        graphics.drawLine(line4X, line4Y, line1X, line1Y);

    }
}
