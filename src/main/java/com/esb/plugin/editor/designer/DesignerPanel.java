package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.editor.SelectListener;
import com.esb.plugin.editor.designer.action.DropActionHandler;
import com.esb.plugin.editor.designer.action.MoveActionHandler;
import com.esb.plugin.editor.designer.action.RemoveActionHandler;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.esb.plugin.graph.layout.FlowGraphLayout;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, DropTargetListener, SnapshotListener, DrawableListener {

    private static final Logger LOG = Logger.getInstance(DesignerPanel.class);

    private static final int WINDOW_GROW_STEP = 110;
    private static final JBColor BACKGROUND_COLOR = JBColor.WHITE;
    private final GraphNode NOTHING_SELECTED = new NothingSelectedNode();
    private final Module module;

    private GraphSnapshot snapshot;
    private SelectListener selectListener;
    private GraphNode selected = NOTHING_SELECTED;

    private int offsetX;
    private int offsetY;
    private boolean updated = false;
    private boolean dragging;

    private FlowGraph graph;

    public DesignerPanel(Module module, GraphSnapshot snapshot) {
        setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        addMouseMotionListener(this);

        this.module = module;
        this.snapshot = snapshot;
        this.snapshot.addListener(this);

        graph = snapshot.getGraph();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        // We compute again the graph layout if and only if it was updated.
        if (updated) {

            LOG.info("Graph changed");

            graph = snapshot.getGraph();

            FlowGraphLayout.compute(graph, g2);

            PrintFlowInfo.debug(graph);

            adjustWindowSize();

            updated = false;
        }

        long start = System.currentTimeMillis();

        // Draw the graph nodes
        graph.breadthFirstTraversal(node -> node.draw(graph, g2, DesignerPanel.this));

        // Draw the arrows connecting the nodes
        graph.breadthFirstTraversal(node -> node.drawArrows(graph, g2, DesignerPanel.this));

        // Draw on top of everything dragged elements of the graph
        graph.breadthFirstTraversal(node -> node.drawDrag(graph, g2, DesignerPanel.this));

        long end = System.currentTimeMillis() - start;

        LOG.info("Painted in " + end + " ms");
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        dragging = true;
        selected.dragging();
        selected.drag(event.getX() - offsetX, event.getY() - offsetY);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        setTheCursor(Cursor.getDefaultCursor());
        graph.nodes().forEach(node -> node.mouseMoved(this, event));
    }

    @Override
    public void mousePressed(MouseEvent event) {
        unselect();
        graph.nodes().forEach(node -> node.mousePressed(this, event));
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;

            int dragX = e.getX();
            int dragY = e.getY();

            selected.drag(dragX, dragY);
            selected.drop();

            Point dragPoint = new Point(dragX, dragY);

            // TODO: Create builder.
            MoveActionHandler handler = new MoveActionHandler(module, snapshot, getGraphics2D(), selected, dragPoint);
            handler.handle();

            // TODO: This repaint might not be necessary
            repaint();
        }
    }

    @Override
    public void drop(DropTargetDropEvent dropEvent) {
        // TODO: Create builder
        DropActionHandler handler = new DropActionHandler(module, snapshot, getGraphics2D(), dropEvent);
        handler.handle();
    }

    @Override
    public void removeComponent(GraphNode nodeToRemove) {
        // TODO: Create builder
        RemoveActionHandler handler = new RemoveActionHandler(snapshot, nodeToRemove);
        handler.handle();
    }

    @Override
    public void select(GraphNode node, MouseEvent event) {
        selected = node;
        selected.selected();

        offsetX = event.getX() - selected.x();
        offsetY = event.getY() - selected.y();

        selectListener.onSelect(snapshot, selected);
    }

    @Override
    public void setTheCursor(Cursor cursor) {
        setCursor(cursor);
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
        SwingUtilities.invokeLater(() -> {
            updated = true;
            unselect();
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

    private Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

    private void unselect() {
        selected.unselected();
        selectListener.onUnselect();
        selected = NOTHING_SELECTED;
    }

    /**
     * If the graph has grown beyond the current window size, we must adapt it.
     */
    private void adjustWindowSize() {
        Collection<GraphNode> nodes = graph.nodes();
        int maxX = nodes.stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = nodes.stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + WINDOW_GROW_STEP;
        int newSizeY = maxY + WINDOW_GROW_STEP;
        Dimension newDimension = new Dimension(newSizeX, newSizeY);
        setSize(newDimension);
        setPreferredSize(newDimension);
    }
}
