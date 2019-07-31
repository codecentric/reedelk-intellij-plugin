package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.AbstractDesignerPanelActionHandler;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.action.add.SubFlowActionNodeAdd;
import com.esb.plugin.graph.action.remove.SubFlowActionNodeRemove;
import com.esb.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowDesignerPanelActionHandler extends AbstractDesignerPanelActionHandler {

    SubFlowDesignerPanelActionHandler(@NotNull Module module, @NotNull FlowSnapshot snapshot) {
        super(module, snapshot);
    }

    @Override
    protected Action getAddAction(Point dropPoint, GraphNode nodeToAdd, Graphics2D graphics, ImageObserver observer) {
        return new SubFlowActionNodeAdd(dropPoint, nodeToAdd, graphics, observer);
    }

    @Override
    protected Action getRemoveAction(GraphNode nodeToRemove) {
        PlaceholderProvider placeholderProvider =
                () -> GraphNodeFactory.get(module, Placeholder.class.getName());
        return new SubFlowActionNodeRemove(nodeToRemove, placeholderProvider);
    }
}
