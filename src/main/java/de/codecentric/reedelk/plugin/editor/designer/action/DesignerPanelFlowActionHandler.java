package de.codecentric.reedelk.plugin.editor.designer.action;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.editor.designer.action.add.FlowActionNodeAdd;
import de.codecentric.reedelk.plugin.editor.designer.action.move.FlowActionNodeReplace;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.FlowActionNodeRemove;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class DesignerPanelFlowActionHandler extends DesignerPanelAbstractActionHandler {

    public DesignerPanelFlowActionHandler(@NotNull Module module, @NotNull FlowSnapshot snapshot) {
        super(module, snapshot);
    }

    @Override
    protected Action getActionAdd(GraphNode nodeToAdd, Point dropPoint, Graphics2D graphics, ImageObserver observer) {
        return new FlowActionNodeAdd(dropPoint, nodeToAdd, graphics, observer, placeholderProvider);
    }

    @Override
    protected Action getActionRemove(GraphNode nodeToRemove) {
        return new FlowActionNodeRemove(nodeToRemove, placeholderProvider);
    }

    @Override
    protected Action getActionReplace(GraphNode nodeFrom, GraphNode nodeTo) {
        return new FlowActionNodeReplace(nodeFrom, nodeTo);
    }
}
