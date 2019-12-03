package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.DesignerWindowSizeCalculator;
import com.reedelk.plugin.commons.PrintFlowInfo;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.editor.designer.debug.CenterOfNodeDrawable;
import com.reedelk.plugin.editor.designer.dnd.DesignerDropTargetListener;
import com.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import com.reedelk.plugin.editor.designer.dnd.MouseListenerAdapter;
import com.reedelk.plugin.editor.designer.hint.HintDrawable;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.editor.designer.hint.HintResultListener;
import com.reedelk.plugin.editor.designer.hint.HintRunnable;
import com.reedelk.plugin.editor.designer.misc.BuildingFlowInfoPanel;
import com.reedelk.plugin.editor.designer.misc.FlowWithErrorInfoPanel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.SnapshotListener;
import com.reedelk.plugin.graph.layout.FlowGraphLayout;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.project.DesignerSelectionManager;
import com.reedelk.plugin.service.project.SelectableItem;
import com.reedelk.plugin.service.project.SelectableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import static com.reedelk.plugin.editor.designer.dnd.DesignerDropTargetListener.DropActionListener;
import static com.reedelk.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;
import static com.reedelk.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener.CURRENT_SELECTION_TOPIC;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public abstract class DesignerPanel extends DisposablePanel implements
        MouseMotionListener, MouseListenerAdapter, SnapshotListener,
        DropActionListener, HintResultListener, DrawableListener,
        ComponentListUpdateNotifier {

    private static final Logger LOG = Logger.getInstance(DesignerPanel.class);

    static final int TOP_PADDING = 80;

    protected transient final FlowSnapshot snapshot;

    private transient final Module module;
    private transient final DesignerPanelActionHandler actionHandler;

    private transient HintResult hintResult = HintResult.EMPTY;
    private transient HintRunnable hintCalculator; // TODO: This one should be removed and created a class to manage move operation
    private transient HintDrawable hintDrawable;

    private transient GraphNode selected;
    private transient SelectableItem currentSelection;
    private transient MessageBusConnection busConnection;
    private transient CenterOfNodeDrawable centerOfNodeDrawable;
    private transient CurrentSelectionListener componentSelectedPublisher;
    private transient FlowWithErrorInfoPanel errorFlowInfoPanel = new FlowWithErrorInfoPanel();
    private transient BuildingFlowInfoPanel buildingFlowInfoPanel = new BuildingFlowInfoPanel();

    private int offsetX;
    private int offsetY;
    private boolean visible;
    private boolean dragging;
    private boolean snapshotUpdated;

    private DesignerSelectionManager designerSelectionManager;


    DesignerPanel(@NotNull Module module,
                  @NotNull FlowSnapshot snapshot,
                  @NotNull DesignerPanelActionHandler actionHandler) {
        this.module = module;
        this.snapshot = snapshot;
        this.actionHandler = actionHandler;
        this.hintDrawable = new HintDrawable();

        this.snapshot.addListener(this);
        this.centerOfNodeDrawable = new CenterOfNodeDrawable(snapshot);

        addMouseListener(this);
        addMouseMotionListener(this);

        this.busConnection = module.getMessageBus().connect();
        this.busConnection.subscribe(COMPONENT_LIST_UPDATE_TOPIC, this);
        this.componentSelectedPublisher = module.getProject().getMessageBus().syncPublisher(CURRENT_SELECTION_TOPIC);
        this.designerSelectionManager = DesignerSelectionManager.getInstance(module.getProject());

        addDropTargetListener(module, snapshot, actionHandler);
        addAncestorListener();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        snapshot.applyOnGraph(graph -> {

                    // Set the canvas background
                    setBackground(Colors.DESIGNER_BG);

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

                    hintDrawable.draw(graph, g2, hintResult, this);

                    // Draw on top of everything dragged elements of the graph
                    graph.breadthFirstTraversal(node -> node.drawDrag(graph, g2, DesignerPanel.this));

                    centerOfNodeDrawable.draw(g2);

                },

                absentFlow -> buildingFlowInfoPanel.draw(g2, this, this),

                flowWithError -> errorFlowInfoPanel.draw(flowWithError, g2, this, this));

        g2.dispose();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (selected != null && selected.isDraggable()) {
            if (!dragging) {
                hintCalculator = HintRunnable.start(snapshot, getGraphics2D(), DesignerPanel.this);
                dragging = true;
                selected.dragging();
            }
            hintCalculator.point(event.getPoint());
            selected.drag(event.getX() - offsetX, event.getY() - offsetY);
            refresh();
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        setTheCursor(Cursor.getDefaultCursor());
        snapshot.applyOnValidGraph(graph ->
                graph.nodes().forEach(node ->
                        node.mouseMoved(DesignerPanel.this, event)));
    }

    @Override
    public void mousePressed(MouseEvent event) {
        // Notify all nodes that the mouse has been pressed
        snapshot.applyOnValidGraph(graph -> {

            graph.nodes().forEach(node -> node.mousePressed(DesignerPanel.this, event));

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
            refresh();
        });
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (!dragging) return;

        hintCalculator.stop();

        dragging = false;

        if (selected != null) {

            int dragX = event.getX();
            int dragY = event.getY();

            selected.drag(dragX, dragY);
            selected.drop();

            Point dragPoint = new Point(dragX, dragY);

            actionHandler.onMove(getGraphics2D(), selected, dragPoint, this);

            refresh();
        }
    }

    @Override
    public void setTheCursor(Cursor cursor) {
        setCursor(cursor);
    }

    @Override
    public void removeComponent(GraphNode nodeToRemove) {
        actionHandler.onRemove(nodeToRemove);
    }

    @Override
    public void onDataChange() {
        snapshotUpdated = true;
        if (visible) {
            // If it is visible and nothing is selected, we need to set default
            // selection. The first time we open the designer, we  need to wait
            // for the background Thread to deserialize the graph. When the
            // graph is de-serialized, we get notified with this method call.
            // If nothing is already selected, we set as current selection
            // the default selected item.
            snapshot.applyOnGraph(graph -> {
                        boolean isAnySelectionPresent = designerSelectionManager.getCurrentSelection().isPresent();
                        if (!isAnySelectionPresent) {
                            select(defaultSelectedItem());
                        }
                    },

                    absentFlow -> unselect(),

                    flowWithError -> unselect());

            // When some graph data is changed we need to repaint the canvas.
            // This is needed for instance to refresh flow (or subflow) and
            // components descriptions properties.
            refresh();
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
            snapshot.applyOnValidGraph(graph ->
                    SwingUtilities.invokeLater(() -> select(defaultSelectedItem())));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        busConnection.disconnect();
    }

    @Override
    public void onNodeAdded(GraphNode addedNode) {
        unselect();
        select(addedNode);
    }

    @Override
    public void onHintResult(HintResult hintResult) {
        this.hintResult = hintResult;
        refresh();
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
        if (node.isSelectable()) {
            // Display the Component Properties Tool Window if it is not visible already
            ToolWindowUtils.ComponentProperties.show(module.getProject());
            selected = node;
            selected.selected();
            currentSelection = new SelectableItemComponent(module, snapshot, selected);
            select(currentSelection);
        }
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
        snapshot.applyOnValidGraph(graph ->
                DesignerWindowSizeCalculator.from(graph, getGraphics2D()).ifPresent(updatedSize -> {
                    setSize(updatedSize);
                    setPreferredSize(updatedSize);
                }));
    }

    private void addDropTargetListener(@NotNull Module module, @NotNull FlowSnapshot snapshot, @NotNull DesignerPanelActionHandler actionHandler) {
        DesignerDropTargetListener dropTargetListener =
                new DesignerDropTargetListener(module, snapshot, actionHandler, this, this, this);
        new DropTarget(this, dropTargetListener);
    }

    private void refresh() {
        SwingUtilities.invokeLater(this::repaint);
    }

    private void addAncestorListener() {
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                snapshot.applyOnValidGraph(graph -> select(defaultSelectedItem()));
                visible = true;
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                unselect();
                visible = false;
            }
        });
    }
}
