package com.reedelk.plugin.editor.designer.action;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.designer.action.add.FlowActionNodeAdd;
import com.reedelk.plugin.editor.designer.action.move.FlowActionNodeReplace;
import com.reedelk.plugin.editor.designer.action.remove.FlowActionNodeRemove;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
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
