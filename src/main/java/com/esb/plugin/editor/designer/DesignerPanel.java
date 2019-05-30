package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.editor.SelectListener;
import com.esb.plugin.editor.designer.action.DropActionHandler;
import com.esb.plugin.editor.designer.action.MoveActionHandler;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.esb.plugin.graph.layout.FlowGraphLayout;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
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
import java.util.Optional;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, DropTargetListener, SnapshotListener {

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

        // Set Antialiasing
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        // We compute again the graph layout if and only if it was updated.
        if (updated) {

            LOG.info("Painting...(updated)");

            graph = snapshot.getGraph();

            FlowGraphLayout.compute(graph, g2);

            PrintFlowInfo.debug(graph);// TODO: debug only

            adjustWindowSize();

            updated = false;

        }

        long start = System.currentTimeMillis();


        // First draw Scoped Nodes,
        // Scoped nodes so that the background of (for instance) otherwise lane for choice
        // is drawn before the nodes on top
        // First MUST draw outermost scoped nodes!!!
        graph.breadthFirstTraversal(node -> {
            if (node instanceof ScopedGraphNode) {
                node.draw(graph, g2, DesignerPanel.this);
            }
        });

        // First draw Scoped Nodes,
        graph.breadthFirstTraversal(node -> {
            if (!(node instanceof ScopedGraphNode)) {
                node.draw(graph, g2, DesignerPanel.this);
            }
        });

        // The selected node must be drawn LAST so
        // that it is on top of all the other drawables.
        graph.nodes()
                .stream()
                .filter(Drawable::isSelected)
                .findFirst()
                .ifPresent(selectedNode -> selectedNode.draw(graph, g2, DesignerPanel.this));

        long end = System.currentTimeMillis() - start;
        LOG.info("Painted... " + end);
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

        Optional<GraphNode> nodeWithinCoordinates = getNodeWithinCoordinates(x, y);
        if (nodeWithinCoordinates.isPresent()) {
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

        Optional<GraphNode> nodeWithinCoordinates = getNodeWithinCoordinates(x, y);
        if (nodeWithinCoordinates.isPresent()) {
            // Unselect the previous one
            select(nodeWithinCoordinates.get());

            offsetX = event.getX() - selected.x();
            offsetY = event.getY() - selected.y();

            if (offsetX > 5 || offsetY > 5) {
                // We start dragging if and only if we move
                // far enough to consider it a drag movement.
                selected.dragging();
                selected.drag(event.getX() - offsetX, event.getY() - offsetY);
                dragging = true;
            }
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

        // TODO: Create builder.
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
        // TODO: Create builder
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
        Collection<GraphNode> nodes = graph.nodes();
        int maxX = nodes.stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = nodes.stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + WINDOW_GROW_STEP;
        int newSizeY = maxY + WINDOW_GROW_STEP;
        Dimension newDimension = new Dimension(newSizeX, newSizeY);
        setSize(newDimension);
        setPreferredSize(newDimension);
    }

    private Optional<GraphNode> getNodeWithinCoordinates(int x, int y) {
        return graph.nodes()
                        .stream()
                        .filter(node -> node.contains(this, x, y))
                        .findFirst();
    }

    private void unselect() {
        selected.unselected();
        selectListener.onUnselect();
        select(NOTHING_SELECTED);
    }

    private void select(GraphNode node) {
        selected = node;
        selected.selected();
        selectListener.onSelect(snapshot, selected);
    }

    private Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

}
