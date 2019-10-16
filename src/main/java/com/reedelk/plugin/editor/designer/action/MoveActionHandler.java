package com.reedelk.plugin.editor.designer.action;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScope;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

public class MoveActionHandler {

    private FlowSnapshot snapshot;
    private Graphics2D graphics;
    private Point dropPoint;
    private Module module;

    private GraphNode replacementNode;
    private GraphNode movedNode;

    private Action actionReplace;
    private Action actionRemove;
    private Action actionAdd;

    private MoveActionHandler() {
    }

    public void handle() {

        if (isDropPointInsideCurrentSelectedNode(dropPoint.x, dropPoint.y)) {
            // We don't move anything if we drop the node within its box boundaries.
            return;
        }

        FlowGraph originalGraph = snapshot.getGraphOrThrowIfAbsent();

        // Copy the original graph
        FlowGraph copy = originalGraph.copy();

        // Remove the dropped node from the copy graph
        actionReplace.execute(copy);

        // Set the position of the replaced node equal to the moved node.
        // This way the ADD action can properly compute the new position of the moved node.
        replacementNode.setPosition(movedNode.x(), movedNode.y());

        // Remove the replaced node from any scope it might belong to
        Optional<ScopedGraphNode> selectedScope = FindScope.of(copy, movedNode);
        selectedScope.ifPresent(scopedNode -> {
            scopedNode.removeFromScope(movedNode);
            scopedNode.addToScope(replacementNode);
        });

        // Add the node. We must decorate the copy with the change aware
        // decorator to understand later on if the graph was actually
        // changed as a result of the ADD node action.
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        // Add the node
        actionAdd.execute(modifiableGraph);

        // If the copy of the graph was changed, then update the snapshot
        // with the new graph, otherwise keep the original graph.
        if (modifiableGraph.isChanged()) {

            // Must remove the replaced node
            actionRemove.execute(modifiableGraph);

            modifiableGraph.commit(module);

            snapshot.updateSnapshot(this, modifiableGraph);

        } else {

            // Add back the node to the scope and remove replacementNode
            // if the original graph did not change.
            selectedScope.ifPresent(scopedNode -> {
                scopedNode.addToScope(movedNode);
                scopedNode.removeFromScope(replacementNode);
            });

            snapshot.updateSnapshot(this, originalGraph);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private GraphNode replacementNode;
        private FlowSnapshot snapshot;
        private Graphics2D graphics;
        private GraphNode movedNode;
        private Point dropPoint;
        private Module module;
        private Action actionReplace;
        private Action actionRemove;
        private Action actionAdd;

        public Builder module(@NotNull Module module) {
            this.module = module;
            return this;
        }

        public Builder dropPoint(@NotNull Point dropPoint) {
            this.dropPoint = dropPoint;
            return this;
        }

        public Builder actionAdd(@NotNull Action actionAdd) {
            this.actionAdd = actionAdd;
            return this;
        }

        public Builder graphics(@NotNull Graphics2D graphics) {
            this.graphics = graphics;
            return this;
        }

        public Builder movedNode(@NotNull GraphNode movedNode) {
            this.movedNode = movedNode;
            return this;
        }

        public Builder snapshot(@NotNull FlowSnapshot snapshot) {
            this.snapshot = snapshot;
            return this;
        }

        public Builder actionRemove(@NotNull Action actionRemove) {
            this.actionRemove = actionRemove;
            return this;
        }

        public Builder actionReplace(@NotNull Action actionReplace) {
            this.actionReplace = actionReplace;
            return this;
        }

        public Builder replacementNode(@NotNull GraphNode replacementNode) {
            this.replacementNode = replacementNode;
            return this;
        }

        public MoveActionHandler build() {
            MoveActionHandler handler = new MoveActionHandler();
            handler.replacementNode = replacementNode;
            handler.dropPoint = dropPoint;
            handler.movedNode = movedNode;
            handler.snapshot = snapshot;
            handler.graphics = graphics;
            handler.module = module;
            handler.actionReplace = actionReplace;
            handler.actionRemove = actionRemove;
            handler.actionAdd = actionAdd;
            return handler;
        }
    }

    /**
     * Checks whether the drop point is inside the current selected box node.
     * The box for a selected node is within X center +(-) half width and
     * Y center +(-) half height.
     *
     * @return true if the drop point is within the moved node box, false if it is outside.
     */
    private boolean isDropPointInsideCurrentSelectedNode(int dropX, int dropY) {
        boolean withinX =
                dropX > movedNode.x() - Half.of(movedNode.width(graphics)) &&
                        dropX < movedNode.x() + Half.of(movedNode.width(graphics));
        boolean withinY =
                dropY > movedNode.y() - Half.of(movedNode.height(graphics)) &&
                        dropY < movedNode.y() + Half.of(movedNode.height(graphics));
        return withinX && withinY;
    }
}
