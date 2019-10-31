package com.reedelk.plugin.editor;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.designer.AbstractDesignerPanelActionHandler;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.add.FlowActionNodeAdd;
import com.reedelk.plugin.graph.action.remove.FlowActionNodeRemove;
import com.reedelk.plugin.graph.action.replace.FlowActionNodeReplace;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class FlowDesignerPanelActionHandler extends AbstractDesignerPanelActionHandler {

    FlowDesignerPanelActionHandler(@NotNull Module module, @NotNull FlowSnapshot snapshot) {
        super(module, snapshot);
    }

    @Override
    protected Action getActionAdd(GraphNode nodeToAdd, Point dropPoint, Graphics2D graphics, ImageObserver observer) {
        return new FlowActionNodeAdd(dropPoint, nodeToAdd, graphics, observer);
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
