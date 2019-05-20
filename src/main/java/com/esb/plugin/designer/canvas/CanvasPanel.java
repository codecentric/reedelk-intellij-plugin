package com.esb.plugin.designer.canvas;

import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.designer.SelectListener;
import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.canvas.action.DropActionHandler;
import com.esb.plugin.designer.canvas.action.MoveActionHandler;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.esb.plugin.graph.layout.FlowGraphLayout;
import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.Optional;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class CanvasPanel extends JBPanel implements MouseMotionListener, MouseListener, DropTargetListener, SnapshotListener {

    private final JBColor BACKGROUND_COLOR = JBColor.WHITE;
    private final GraphNode NOTHING_SELECTED = new NothingSelectedNode();
    private final Module module;

    private GraphSnapshot snapshot;
    private SelectListener selectListener;
    private GraphNode selected = NOTHING_SELECTED;

    private int offsetX;
    private int offsetY;
    private boolean updated = true;
    private boolean dragging;

    public CanvasPanel(Module module, GraphSnapshot snapshot, AncestorListener listener) {
        setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        addMouseMotionListener(this);
        addAncestorListener(listener);

        this.module = module;
        this.snapshot = snapshot;
        this.snapshot.addListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        FlowGraph graph = snapshot.getGraph();

        // Set Antialiasing
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        // We compute again the graph layout if and only if it was updated.
        if (updated) {

            FlowGraphLayout.compute(graph, g2);

            PrintFlowInfo.debug(graph);// TODO: debug only

            adjustWindowSize();

            updated = false;
        }

        Collection<GraphNode> nodes = graph.nodes();

        // Draw each node of the graph
        // (except the current selected Drawable - see below)
        nodes.stream()
                .filter(node -> !node.isSelected())
                .forEach(node -> node.draw(graph, g2, CanvasPanel.this));

        // The selected node must be drawn LAST so
        // that it is on top of all the other drawables.
        nodes.stream()
                .filter(GraphNode::isSelected)
                .findFirst()
                .ifPresent(drawable -> drawable.draw(graph, g2, CanvasPanel.this));
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (dragging) {
            selected.drag(event.getX() - offsetX, event.getY() - offsetY);
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        Optional<GraphNode> drawableWithinCoordinates = getDrawableWithinCoordinates(x, y);
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

        unselect();

        Optional<GraphNode> drawableWithinCoordinates = getDrawableWithinCoordinates(x, y);
        if (drawableWithinCoordinates.isPresent()) {
            // Unselect the previous one
            select(drawableWithinCoordinates.get());
            selected.dragging();

            offsetX = event.getX() - selected.x();
            offsetY = event.getY() - selected.y();
            selected.drag(event.getX() - offsetX, event.getY() - offsetY);

            dragging = true;
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!dragging) return;

        dragging = false;

        int dragX = e.getX();
        int dragY = e.getY();

        selected.drag(dragX, dragY);
        selected.drop();

        Point dragPoint = new Point(dragX, dragY);

        new MoveActionHandler(
                module,
                snapshot,
                getGraphics2D(),
                selected,
                dragPoint)
                .handle();

        repaint();
    }

    @Override
    public void drop(DropTargetDropEvent dropEvent) {
        new DropActionHandler(
                module,
                snapshot,
                getGraphics2D(),
                dropEvent)
                .handle();
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        unselect();
        repaint();
    }

    @Override
    public void onDataChange(@NotNull FlowGraph graph) {
        SwingUtilities.invokeLater(() -> {
            updated = true;
            invalidate();
            repaint();
        });
    }

    @Override
    public void onStructureChange(@NotNull FlowGraph graph) {
        unselect();
        SwingUtilities.invokeLater(() -> {
            updated = true;
            invalidate();
            repaint();
        });
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

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // nothing to do
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // nothing to do
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // nothing to do
    }

    public void addListener(SelectListener listener) {
        this.selectListener = listener;
    }

    /**
     * If the graph has grown beyond the current window size, we must adapt it.
     */
    private void adjustWindowSize() {
        // TODO: not efficient at all...
        int maxX = snapshot.getGraph().nodes().stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = snapshot.getGraph().nodes().stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + Tile.WIDTH;
        int newSizeY = maxY + Tile.HEIGHT;
        Dimension newDimension = new Dimension(newSizeX, newSizeY);
        setSize(newDimension);
        setPreferredSize(newDimension);
    }

    private Optional<GraphNode> getDrawableWithinCoordinates(int x, int y) {
        return snapshot.getGraph() == null ? Optional.empty() :
                snapshot.getGraph().nodes()
                        .stream()
                        .filter(drawable -> drawable.contains(this, x, y))
                        .findFirst();
    }

    private void unselect() {
        selected.unselected();
        selectListener.onUnselect();
        select(NOTHING_SELECTED);
    }

    private void select(GraphNode drawable) {
        selected = drawable;
        selected.selected();
        selectListener.onSelect(snapshot, selected);
    }

    private Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

}
