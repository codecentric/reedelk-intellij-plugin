package de.codecentric.reedelk.plugin.editor.designer.dnd;

import de.codecentric.reedelk.plugin.editor.designer.hint.HintResultListener;
import de.codecentric.reedelk.plugin.editor.designer.hint.HintRunnable;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.editor.properties.CommitPropertiesListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.ImageObserver;
import java.util.Optional;

import static de.codecentric.reedelk.plugin.commons.Topics.COMMIT_COMPONENT_PROPERTIES_EVENTS;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;

public class DesignerDropTargetListener implements DropTargetListener {

    private final DesignerPanelActionHandler actionHandler;
    private final DropActionListener dropActionListener;
    private final HintResultListener hintResultListenerListener;
    private final ImageObserver imageObserver;
    private final FlowSnapshot snapshot;

    private CommitPropertiesListener commitPublisher;
    private HintRunnable hintCalculator;


    public DesignerDropTargetListener(@NotNull Module module,
                                      @NotNull FlowSnapshot snapshot,
                                      @NotNull DesignerPanelActionHandler actionHandler,
                                      @NotNull DropActionListener dropActionListener,
                                      @NotNull HintResultListener hintResultListenerListener,
                                      @NotNull ImageObserver imageObserver) {
        this.commitPublisher = module.getProject().getMessageBus().syncPublisher(COMMIT_COMPONENT_PROPERTIES_EVENTS);
        this.hintResultListenerListener = hintResultListenerListener;
        this.dropActionListener = dropActionListener;
        this.imageObserver = imageObserver;
        this.actionHandler = actionHandler;
        this.snapshot = snapshot;
    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {
        Graphics2D graphics2D = (Graphics2D) event.getDropTargetContext().getComponent().getGraphics();
        hintCalculator = HintRunnable.start(snapshot, graphics2D, hintResultListenerListener);
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
        hintCalculator.point(event.getLocation());
    }

    @Override
    public void dragExit(DropTargetEvent event) {
        hintCalculator.stop();
    }

    @Override
    public void drop(DropTargetDropEvent event) {
        hintCalculator.stop();

        // Drop operation can only be applied on a valid graph.
        // A graph is valid if and only if it does not contain
        // errors and it is not null in the current snapshot.
        snapshot.applyOnValidGraph(graph -> {

            // Before executing an action which modifies the Graph we *MUST* commit
            // all the pending changes not closed in the PropertiesPanel. For example
            // the Router Condition -> Route table uses this event to commit the table's
            // cell editor before dropping a new node into the graph.
            commitPublisher.onCommit();

            Graphics2D g2 = (Graphics2D) event.getDropTargetContext().getComponent().getGraphics();

            Optional<GraphNode> addedNode =
                    actionHandler.onAdd(g2, event.getLocation(), event.getTransferable(), imageObserver);

            // If the result of the action was a node added to the graph,
            // we must accept the DROP action and notify the listener about
            // the newly added node. Otherwise we reject the drop action.
            if (addedNode.isPresent()) {
                event.acceptDrop(ACTION_COPY_OR_MOVE);
                dropActionListener.onNodeAdded(addedNode.get());
            } else {
                event.rejectDrop();
            }
        });
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // nothing to do
    }

    public interface DropActionListener {
        void onNodeAdded(GraphNode addedNode);
    }
}
