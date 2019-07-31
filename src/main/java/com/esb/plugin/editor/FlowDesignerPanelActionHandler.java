package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.AbstractDesignerPanelActionHandler;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.action.add.FlowActionNodeAdd;
import com.esb.plugin.graph.action.remove.FlowActionNodeRemove;
import com.esb.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class FlowDesignerPanelActionHandler extends AbstractDesignerPanelActionHandler {

    FlowDesignerPanelActionHandler(@NotNull Module module, @NotNull FlowSnapshot snapshot) {
        super(module, snapshot);
    }

    @Override
    protected Action getAddAction(Point dropPoint, GraphNode nodeToAdd, Graphics2D graphics, ImageObserver observer) {
        return new FlowActionNodeAdd(dropPoint, nodeToAdd, graphics, observer);
    }

    @Override
    protected Action getRemoveAction(GraphNode nodeToRemove) {
        PlaceholderProvider placeholderProvider =
                () -> GraphNodeFactory.get(module, Placeholder.class.getName());
        return new FlowActionNodeRemove(nodeToRemove, placeholderProvider);
    }
}
