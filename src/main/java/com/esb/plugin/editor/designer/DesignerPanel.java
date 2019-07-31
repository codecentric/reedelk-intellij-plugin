package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.commons.DesignerWindowSizeCalculator;
import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.component.scanner.ComponentListUpdateNotifier;
import com.esb.plugin.editor.designer.widget.CenterOfNodeDrawable;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.SnapshotListener;
import com.esb.plugin.graph.layout.FlowGraphLayout;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.service.project.SelectableItem;
import com.esb.plugin.service.project.SelectableItemComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.messages.MessageBusConnection;
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

import static com.esb.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public abstract class DesignerPanel extends JBPanel implements
        MouseMotionListener,
        MouseListener,
        DropTargetListener,
        SnapshotListener,
        DrawableListener,
        ComponentListUpdateNotifier {

    private static final Logger LOG = Logger.getInstance(DesignerPanel.class);

    final int TOP_PADDING = 80;

    protected FlowSnapshot snapshot;

    private final Module module;
    private final DesignerPanelActionHandler actionHandler;

    private int offsetX;
    private int offsetY;
    private boolean dragging;
    private boolean snapshotUpdated = false;

    private GraphNode selected;
    private SelectableItem currentSelection;
    private CenterOfNodeDrawable centerOfNodeDrawable;
    private CurrentSelectionListener componentSelectedPublisher;

    private boolean visible = false;

    DesignerPanel(@NotNull Module module,
                  @NotNull FlowSnapshot snapshot,
                  @NotNull DesignerPanelActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.module = module;
        this.snapshot = snapshot;
        this.snapshot.addListener(this);

        this.centerOfNodeDrawable = new CenterOfNodeDrawable(snapshot);

        setBackground(Colors.DESIGNER_BG);
        addMouseListener(this);
        addMouseMotionListener(this);

        MessageBusConnection connect = module.getMessageBus().connect();
        connect.subscribe(COMPONENT_LIST_UPDATE_TOPIC, this);

        componentSelectedPublisher = module
                .getProject()
                .getMessageBus()
                .syncPublisher(CurrentSelectionListener.CURRENT_SELECTION_TOPIC);

        addAncestorListener();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        FlowGraph graph = snapshot.getGraph();

        // We compute again the graph layout if and
        // only if the graph snapshot it was updated.
        if (snapshotUpdated) {

            LOG.info("Graph changed");

            FlowGraphLayout.compute(graph, g2, TOP_PADDING);

            adjustWindowSize();

            PrintFlowInfo.debug(graph);

            snapshotUpdated = false;

        }

        beforePaint(g2);

        // Draw the graph nodes
        graph.breadthFirstTraversal(node -> node.draw(graph, g2, DesignerPanel.this));

        // Draw the arrows connecting the nodes
        graph.breadthFirstTraversal(node -> node.drawArrows(graph, g2, DesignerPanel.this));

        // Draw on top of everything dragged elements of the graph
        graph.breadthFirstTraversal(node -> node.drawDrag(graph, g2, DesignerPanel.this));

        centerOfNodeDrawable.draw(g2);

        g2.dispose();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (selected != null) {
            dragging = true;
            selected.dragging();
            selected.drag(event.getX() - offsetX, event.getY() - offsetY);
            repaint();
        }
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

            GraphNode selectedNde = selected.get();

            unselect();
            select(selectedNde);

            offsetX = event.getX() - selectedNde.x();
            offsetY = event.getY() - selectedNde.y();

        } else {
            // Nothing is selected, we display flow properties
            unselect();
            select(defaultSelectedItem());
        }

        // Repaint all nodes
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!dragging) return;

        dragging = false;

        if (selected != null) {

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
        // If the drop event was successful we select the newly
        // added Graph Node.
        actionHandler
                .onAdd(getGraphics2D(), dropEvent, this)
                .ifPresent(addedNode -> {
                    unselect();
                    select(addedNode);
                });
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
        repaint();
    }

    @Override
    public void onDataChange() {
        snapshotUpdated = true;
        if (visible) {
            // When some graph data is changed we need to repaint the canvas.
            // This is needed for instance to refresh flow (or subflow) and
            // components descriptions properties.
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    @Override
    public void onComponentListUpdate(Module module) {
        if (visible) {
            // When the component list is updated or we click on the 'compile' button
            // the graph is de-serialized to apply changes and refresh properties
            // which might have been changed from custom Java components. Therefore
            // it is important to reset the current selection to the flow otherwise
            // the selection would be bound to the old object before refreshing
            // the flow (or subflow) graph.
            SwingUtilities.invokeLater(() -> select(defaultSelectedItem()));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
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

    protected abstract void beforePaint(Graphics2D graphics);

    protected abstract SelectableItem defaultSelectedItem();

    private Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

    private void unselect() {
        if (selected != null) {
            selected.unselected();
            selected = null;
        }
        if (currentSelection != null) {
            componentSelectedPublisher.onUnSelected(currentSelection);
        }
    }

    private void select(GraphNode node) {
        selected = node;
        selected.selected();
        currentSelection = new SelectableItemComponent(module, snapshot, selected);
        select(currentSelection);
    }

    private void select(SelectableItem selectableItem) {
        currentSelection = selectableItem;
        componentSelectedPublisher.onSelection(selectableItem);
    }

    /**
     * If the graph has grown beyond the current window size,
     * (horizontally or vertically) we must  adapt the window size accordingly.
     */
    private void adjustWindowSize() {
        DesignerWindowSizeCalculator.from(snapshot.getGraph(), getGraphics2D()).ifPresent(dimension -> {
            setSize(dimension);
            setPreferredSize(dimension);
        });
    }

    private void addAncestorListener() {
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                super.ancestorAdded(event);
                select(defaultSelectedItem());
                visible = true;
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                super.ancestorRemoved(event);
                unselect();
                visible = false;
            }
        });
    }
}
