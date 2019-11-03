package com.reedelk.plugin.editor;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.designer.AbstractDesignerPanelActionHandler;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.add.SubFlowActionNodeAdd;
import com.reedelk.plugin.graph.action.remove.SubFlowActionNodeRemove;
import com.reedelk.plugin.graph.action.replace.SubFlowActionNodeReplace;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowDesignerPanelActionHandler extends AbstractDesignerPanelActionHandler {

    SubFlowDesignerPanelActionHandler(@NotNull Module module, @NotNull FlowSnapshot snapshot) {
        super(module, snapshot);
    }

    @Override
    protected Action getActionAdd(GraphNode nodeToAdd, Point dropPoint, Graphics2D graphics, ImageObserver observer) {
        return new SubFlowActionNodeAdd(dropPoint, nodeToAdd, graphics, observer, placeholderProvider);
    }

    @Override
    protected Action getActionRemove(GraphNode nodeToRemove) {
        return new SubFlowActionNodeRemove(nodeToRemove, placeholderProvider);
    }

    @Override
    protected Action getActionReplace(GraphNode nodeFrom, GraphNode nodeTo) {
        return new SubFlowActionNodeReplace(nodeFrom, nodeTo);
    }
}
