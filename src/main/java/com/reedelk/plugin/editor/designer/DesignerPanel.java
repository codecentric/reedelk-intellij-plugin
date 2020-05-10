package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.*;
import com.reedelk.plugin.editor.designer.debug.CenterOfNodeDrawable;
import com.reedelk.plugin.editor.designer.debug.PrintFlowInfo;
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
import com.reedelk.plugin.editor.properties.selection.SelectableItem;
import com.reedelk.plugin.editor.properties.selection.SelectableItemComponent;
import com.reedelk.plugin.editor.properties.selection.SelectionChangeListener;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.SnapshotListener;
import com.reedelk.plugin.graph.layout.FlowGraphLayout;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.module.ModuleDTO;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.Optional;

import static com.reedelk.plugin.editor.designer.dnd.DesignerDropTargetListener.DropActionListener;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public abstract class DesignerPanel extends DisposablePanel implements
        MouseMotionListener, MouseListenerAdapter, SnapshotListener,
        DropActionListener, HintResultListener, DrawableListener,
        PlatformModuleService.ModuleChangeNotifier {

    static final int TOP_PADDING = 80;

    private final transient Module module;
    private final transient FlowSnapshot snapshot;
    private final transient SelectableItem defaultSelectedItem;
    private final transient DesignerPanelActionHandler actionHandler;
    private final transient SelectionChangeListener currentComponentPublisher;

    private transient HintResult hintResult = HintResult.EMPTY;
    private transient HintRunnable hintCalculator;
    private transient HintDrawable hintDrawable;

    private transient GraphNode selected;
    private transient SelectableItem currentSelection;
    private transient MessageBusConnection busConnection;
    private transient CenterOfNodeDrawable centerOfNodeDrawable;
    private transient FlowWithErrorInfoPanel errorFlowInfoPanel = new FlowWithErrorInfoPanel();
    private transient BuildingFlowInfoPanel buildingFlowInfoPanel = new BuildingFlowInfoPanel();

    private int offsetX;
    private int offsetY;
    private boolean isVisible;
    private boolean dragging;
    private boolean snapshotUpdated;


    DesignerPanel(@NotNull Module module,
                  @NotNull FlowSnapshot snapshot,
                  @NotNull DesignerPanelActionHandler actionHandler,
                  @NotNull SelectableItem defaultSelectedItem) {
        this.module = module;
        this.snapshot = snapshot;
        this.actionHandler = actionHandler;
        this.hintDrawable = new HintDrawable();
        this.defaultSelectedItem = defaultSelectedItem;

        this.snapshot.addListener(this);
        this.centerOfNodeDrawable = new CenterOfNodeDrawable(snapshot);

        addMouseListener(this);
        addMouseMotionListener(this);

        this.busConnection = module.getMessageBus().connect();
        this.busConnection.subscribe(Topics.COMPONENTS_UPDATE_EVENTS, this);
        this.currentComponentPublisher = module.getProject().getMessageBus().syncPublisher(Topics.CURRENT_COMPONENT_SELECTION_EVENTS);

        addDropTargetListener(module, snapshot, actionHandler);
        addAncestorListener();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        snapshot.applyOnGraph(flowGraph -> {

                    // Set the canvas background
                    setBackground(Colors.DESIGNER_BG);

                    // We compute again the graph layout if and
                    // only if the graph snapshot it was updated.
                    if (snapshotUpdated) {

                        FlowGraphLayout.compute(flowGraph, g2, TOP_PADDING);

                        adjustWindowSize();

                        PrintFlowInfo.debug(flowGraph);

                        snapshotUpdated = false;

                    }

                    beforePaint(flowGraph, g2, this);

                    flowGraph.breadthFirstTraversal(node -> {

                        // Draw the graph nodes
                        node.draw(flowGraph, g2, DesignerPanel.this);

                        // Draw the arrows connecting the nodes
                        node.drawArrows(flowGraph, g2, DesignerPanel.this);
                    });

                    hintDrawable.draw(flowGraph, g2, hintResult, this);

                    // Draw on top of everything dragged elements of the graph
                    flowGraph.breadthFirstTraversal(node -> node.drawDrag(flowGraph, g2, DesignerPanel.this));

                    centerOfNodeDrawable.draw(g2);

                    if (currentSelection == null) {
                        currentComponentPublisher.unselect();
                    }

                },

                absentFlow -> {
                    // If the flow is not present we must unselect any property panel
                    // and resize the window to match the parent (scroll pane).
                    Dimension parentSize = getParent().getSize();
                    setSize(parentSize);
                    setPreferredSize(parentSize);
                    buildingFlowInfoPanel.draw(g2, this, this);
                    currentComponentPublisher.unselect();
                },

                flowWithError -> {
                    // If the flow is not present we must unselect any property panel
                    // and resize the window to match the parent (scroll pane).
                    Dimension parentSize = getParent().getSize();
                    setSize(parentSize);
                    setPreferredSize(parentSize);
                    errorFlowInfoPanel.draw(flowWithError, g2, this, this);
                    currentComponentPublisher.unselect();
                });

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
            Optional<GraphNode> toSelectNode = graph.nodes()
                    .stream()
                    .filter(node -> node.contains(DesignerPanel.this, x, y))
                    .findFirst();

            if (toSelectNode.isPresent()) {

                GraphNode selectedNde = toSelectNode.get();

                unselect();
                select(selectedNde);

                offsetX = event.getX() - selectedNde.x();
                offsetY = event.getY() - selectedNde.y();

            } else {
                // Nothing is selected, we display flow properties
                unselect();
                select(defaultSelectedItem, true);
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
        if (isVisible) {
            // The first time we open the designer, we  need to wait
            // for the background Thread to deserialize the graph. When the
            // graph is de-serialized, we get notified with this method call.
            snapshot.applyOnGraph(graph ->
                            select(currentSelection, false),
                            absentFlow -> unselect(),
                            flowWithError -> unselect());

            // When some graph data is changed we need to repaint the canvas.
            // This is needed for instance to refresh flow (or subflow) and
            // components descriptions properties.
            refresh();
        }
    }

    @Override
    public void onModuleChange(Collection<ModuleDTO> components) {
        if (isVisible) {
            // When the component list is updated or we click on the 'compile' button
            // the graph is de-serialized to apply changes and refresh properties
            // which might have been changed from custom Java components. Therefore
            // it is important to reset the current selection to the flow otherwise
            // the selection would be bound to the old object before refreshing
            // the flow (or subflow) graph.
            snapshot.applyOnValidGraph(graph ->
                    ApplicationManager.getApplication().invokeLater(() ->
                            select(defaultSelectedItem, false)));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        currentComponentPublisher.unselect();
        DisposableUtils.dispose(busConnection);
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

    protected abstract void beforePaint(FlowGraph graph, Graphics2D graphics, ImageObserver imageObserver);

    private Graphics2D getGraphics2D() {
        return (Graphics2D) getGraphics();
    }

    private void unselect() {
        if (selected != null) {
            selected.unselected();
            selected = null;
        }
    }

    private void select(GraphNode node) {
        if (node.isSelectable()) {
            selected = node;
            selected.selected();
            select(new SelectableItemComponent(module, snapshot, selected), true);
        }
    }

    private void select(SelectableItem selectableItem, boolean show) {
        currentSelection = selectableItem;
        currentComponentPublisher.onSelection(currentSelection);
        if (show) {
            ToolWindowUtils.showPropertiesPanelToolWindow(module.getProject(),
                    () -> currentComponentPublisher.onSelection(currentSelection));
        }
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
        ApplicationManager.getApplication()
                .invokeLater(this::repaint);
    }

    private void addAncestorListener() {
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                isVisible = true;
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                isVisible = false;
                unselect();
                currentSelection = null;
            }
        });
    }
}
