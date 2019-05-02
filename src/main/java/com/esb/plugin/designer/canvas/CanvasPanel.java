package com.esb.plugin.designer.canvas;

import com.esb.plugin.commons.DebugGraphNodesPosition;
import com.esb.plugin.designer.SelectListener;
import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.canvas.action.DropActionHandler;
import com.esb.plugin.designer.canvas.action.MoveActionHandler;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.FlowGraphLayout;
import com.esb.plugin.graph.manager.JsonChangeNotifier;
import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;

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

import static com.google.common.base.Preconditions.checkState;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class CanvasPanel extends JBPanel implements MouseMotionListener, MouseListener, DropTargetListener, JsonChangeNotifier {

    private final JBColor BACKGROUND_COLOR = JBColor.WHITE;
    private final GraphNode NOTHING_SELECTED = new NothingSelectedNode();
    private final Module module;

    private FlowGraph graph;
    private VirtualFile relatedFile;
    private GraphNode selected = NOTHING_SELECTED;
    private SelectListener selectListener;

    private int offsetx;
    private int offsety;
    private boolean updated;
    private boolean dragging;


    public CanvasPanel(Module module, VirtualFile relatedFile) {
        setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        addMouseMotionListener(this);

        this.module = module;
        this.module.getMessageBus().connect().subscribe(JsonChangeNotifier.TOPIC, this);

        this.relatedFile = relatedFile;
    }

    public void addSelectListener(SelectListener listener) {
        this.selectListener = listener;
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
            DebugGraphNodesPosition.debug(graph);// TODO: debug only
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
    public void onChange(FlowGraph updatedGraph, VirtualFile relatedFile) {
        checkState(updatedGraph != null, "Updated graph must not be null");
        checkState(relatedFile != null, "Related file must not be null");

        // We update the graph only if the change was related to the
        // file managed by this canvas.
        if (this.relatedFile.equals(relatedFile)) {
            updateGraph(updatedGraph);
        }
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

            offsetx = event.getX() - selected.x();
            offsety = event.getY() - selected.y();
            selected.drag(event.getX() - offsetx, event.getY() - offsety);

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

        MoveActionHandler delegate = new MoveActionHandler(module, graph, getGraphics2D(), selected, dragPoint);
        delegate.handle().ifPresent(this::updateGraph);

        repaint();
    }

    @Override
    public void drop(DropTargetDropEvent dropEvent) {
        DropActionHandler delegate = new DropActionHandler(module, graph, getGraphics2D(), dropEvent);
        Optional<FlowGraph> updatedGraph = delegate.handle();
        updatedGraph.ifPresent(this::updateGraph);
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
    public void dragEnter(DropTargetDragEvent dtde) {
        unselect();
        repaint();
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO: Might be useful
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO: Might be useful
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        System.out.println("Drag exit");
    }

    /**
     * If the graph has grown beyond the current window size, we must adapt-it.
     */
    private void adjustWindowSize() {
        int maxX = graph.nodes().stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = graph.nodes().stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + Tile.WIDTH;
        int newSizeY = maxY + Tile.HEIGHT;
        Dimension newDimension = new Dimension(newSizeX, newSizeY);
        setSize(newDimension);
        setPreferredSize(newDimension);
    }

    private Optional<GraphNode> getDrawableWithinCoordinates(int x, int y) {
        //TODO: Graph should never be null.
        return graph == null ? Optional.empty() :
                graph.nodes()
                        .stream()
                        .filter(drawable -> drawable.contains(this, x, y))
                        .findFirst();
    }

    private void unselect() {
        selected.unselected();
        selectListener.onUnselect(graph, selected);
        select(NOTHING_SELECTED);
    }

    private void select(GraphNode drawable) {
        selected = drawable;
        selected.selected();
        selectListener.onSelect(graph, selected);
    }

    private Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

    private void updateGraph(FlowGraph updatedGraph) {
        SwingUtilities.invokeLater(() -> {
            graph = updatedGraph;
            updated = true;
            invalidate();
            repaint();
        });
    }

}
