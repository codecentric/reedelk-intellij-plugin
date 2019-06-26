package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.editor.designer.widget.CenterOfNode;
import com.esb.plugin.editor.designer.widget.FlowMetadata;
import com.esb.plugin.editor.designer.widget.InboundLane;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.esb.plugin.graph.layout.FlowGraphLayout;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.JBColor;
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
import java.util.Collection;
import java.util.Optional;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, DropTargetListener, SnapshotListener, DrawableListener {

    private static final Logger LOG = Logger.getInstance(DesignerPanel.class);

    private final int TOP_PADDING = 80;
    private static final int WINDOW_GROW_STEP = 110;
    private static final JBColor BACKGROUND_COLOR = JBColor.WHITE;
    private final GraphNode NOTHING_SELECTED = new NothingSelectedNode();

    private FlowSnapshot snapshot;
    private SelectListener selectListener;
    private GraphNode selected = NOTHING_SELECTED;

    private int offsetX;
    private int offsetY;
    private boolean updated = false;
    private boolean dragging;

    private InboundLane inboundLane;
    private FlowMetadata flowMetadata;
    private CenterOfNode centerOfNode;

    private final DesignerPanelActionHandler actionHandler;


    public DesignerPanel(FlowSnapshot snapshot, DesignerPanelActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.snapshot = snapshot;
        this.snapshot.addListener(this);

        this.centerOfNode = new CenterOfNode(snapshot);
        this.inboundLane = new InboundLane(snapshot, TOP_PADDING);
        this.flowMetadata = new FlowMetadata(snapshot, TOP_PADDING);

        setBackground(BACKGROUND_COLOR);
        registerAncestorListener();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

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

        inboundLane.draw(g2);

        flowMetadata.draw(g2);

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
            this.selected.unselected();
            select(selected.get(), event);
        } else {
            unselect();
        }

        // Repaint all the nodes
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

            repaint();
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
        selected = NOTHING_SELECTED;
        selectListener.onSelect(snapshot, selected);
    }

    private void select(GraphNode node, MouseEvent event) {
        selected = node;
        selected.selected();

        offsetX = event.getX() - selected.x();
        offsetY = event.getY() - selected.y();

        selectListener.onSelect(snapshot, selected);
    }

    /**
     * If the graph has grown beyond the current window size, we must adapt it.
     */
    private void adjustWindowSize() {
        // No need to adjust window size if the graph is empty.
        FlowGraph graph = snapshot.getGraph();
        if (graph.isEmpty()) return;

        Collection<GraphNode> nodes = graph.nodes();

        int maxX = nodes.stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = nodes.stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + WINDOW_GROW_STEP;
        int newSizeY = maxY + WINDOW_GROW_STEP;
        Dimension newDimension = new Dimension(newSizeX, newSizeY);
        setSize(newDimension);
        setPreferredSize(newDimension);
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
