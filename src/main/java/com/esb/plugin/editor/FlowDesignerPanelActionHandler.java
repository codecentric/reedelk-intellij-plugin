package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.AbstractDesignerPanelActionHandler;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.action.ActionNodeAdd;
import com.esb.plugin.graph.action.FlowActionNodeAdd;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.image.ImageObserver;

public class FlowDesignerPanelActionHandler extends AbstractDesignerPanelActionHandler {

    FlowDesignerPanelActionHandler(Module module, GraphSnapshot snapshot) {
        super(module, snapshot);
    }

    @Override
    protected ActionNodeAdd getActionNodeAdd(Point dropPoint, GraphNode nodeToAdd, Graphics2D graphics, ImageObserver observer) {
        return new FlowActionNodeAdd(dropPoint, nodeToAdd, graphics, observer);
    }
}
