package com.reedelk.plugin.editor;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.designer.AbstractDesignerPanelActionHandler;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.add.FlowActionNodeAdd;
import com.reedelk.plugin.graph.action.remove.FlowActionNodeRemove;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.GraphNodeFactory;
import com.reedelk.runtime.component.Placeholder;
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
