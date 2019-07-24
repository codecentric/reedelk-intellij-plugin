package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.commons.DesignerWindowSizeCalculator;
import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.editor.designer.widget.CenterOfNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.esb.plugin.graph.layout.FlowGraphLayout;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public abstract class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, DropTargetListener, SnapshotListener, DrawableListener {

    private static final Logger LOG = Logger.getInstance(DesignerPanel.class);

    protected final int TOP_PADDING = 80;

    private final GraphNode NOTHING_SELECTED = new NothingSelectedNode();

    protected FlowSnapshot snapshot;
    private SelectListener selectListener;
    private GraphNode selected = NOTHING_SELECTED;

    private int offsetX;
    private int offsetY;
    private boolean updated = false;
    private boolean dragging;

    private CenterOfNode centerOfNode;

    private final DesignerPanelActionHandler actionHandler;


    public DesignerPanel(FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.snapshot = snapshot;
        this.snapshot.addListener(this);

        this.centerOfNode = new CenterOfNode(snapshot);

        setBackground(Colors.DESIGNER_BG);
        registerAncestorListener();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    protected abstract void onPrePaint(Graphics2D graphics);

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        FlowGraph graph = snapshot.getGraph();

        // We compute again the graph layout if and only if it was updated.
        if (updated) {

            LOG.info("Graph changed");

            FlowGraphLayout.compute(graph, g2, TOP_PADDING);

            adjustWindowSize();

            PrintFlowInfo.debug(graph);

            updated = false;

        }

        onPrePaint(g2);

        long start = System.currentTimeMillis();

        // Draw the graph nodes
        graph.breadthFirstTraversal(node -> node.draw(graph, g2, DesignerPanel.this));

        // Draw the arrows connecting the nodes
        graph.breadthFirstTraversal(node -> node.drawArrows(graph, g2, DesignerPanel.this));

        // Draw on top of everything dragged elements of the graph
        graph.breadthFirstTraversal(node -> node.drawDrag(graph, g2, DesignerPanel.this));

        long end = System.currentTimeMillis() - start;

        LOG.info("Painted in " + end + " ms");

        centerOfNode.draw(g2);

        g2.dispose();
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
        snapshot.getGraph().nodes().forEach(node -> node.mouseMoved(this, event));
    }

    @Override
    public void mousePressed(MouseEvent event) {
        // Notify all nodes that the mouse has been pressed
        FlowGraph graph = snapshot.getGraph();
        graph.nodes().forEach(node -> node.mousePressed(this, event));

        // Select the component under the current mouse coordinates
        int x = event.getX();
        int y = event.getY();
        Optional<GraphNode> selected = graph.nodes()
                .stream()
                .filter(node -> node.contains(DesignerPanel.this, x, y))
                .findFirst();

        if (selected.isPresent()) {

            unselect();

            GraphNode selectedNde = selected.get();
            select(selectedNde);

            offsetX = event.getX() - selectedNde.x();
            offsetY = event.getY() - selectedNde.y();

        } else {
            unselect();
            select(NOTHING_SELECTED);
        }

        // Repaint all nodes
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!dragging) return;

        dragging = false;

        if (selected != NOTHING_SELECTED) {

            int dragX = e.getX();
            int dragY = e.getY();

            selected.drag(dragX, dragY);
            selected.drop();

            Point dragPoint = new Point(dragX, dragY);

            actionHandler.onMove(getGraphics2D(), selected, dragPoint, this);

            SwingUtilities.invokeLater(this::repaint);
        }
    }

    @Override
    public void drop(DropTargetDropEvent dropEvent) {
        actionHandler.onDrop(getGraphics2D(), dropEvent, this);
    }

    @Override
    public void removeComponent(GraphNode nodeToRemove) {
        actionHandler.onRemove(nodeToRemove);
    }

    @Override
    public void setTheCursor(Cursor cursor) {
        setCursor(cursor);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        unselect();
        select(NOTHING_SELECTED);
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
            select(NOTHING_SELECTED);
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

    private void select(GraphNode node) {
        selected = node;
        selected.selected();
        selectListener.onSelect(snapshot, selected);
    }

    /**
     * If the graph has grown beyond the current window size,
     * we must adapt it.
     */
    private void adjustWindowSize() {
        DesignerWindowSizeCalculator
                .from(snapshot.getGraph(), getGraphics2D())
                .ifPresent(dimension -> {
                    setSize(dimension);
                    setPreferredSize(dimension);
                });
    }

    private void registerAncestorListener() {
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorRemoved(AncestorEvent event) {
                super.ancestorRemoved(event);
                unselect();
            }
        });
    }
}
