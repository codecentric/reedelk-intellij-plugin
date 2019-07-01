package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.AbstractDesignerPanelActionHandler;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.add.ActionNodeAdd;
import com.esb.plugin.graph.action.add.SubFlowActionNodeAdd;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowDesignerPanelActionHandler extends AbstractDesignerPanelActionHandler {

    SubFlowDesignerPanelActionHandler(Module module, FlowSnapshot snapshot) {
        super(module, snapshot);
    }

    @Override
    protected ActionNodeAdd getActionAdd(Point dropPoint, GraphNode nodeToAdd, Graphics2D graphics, ImageObserver observer) {
        return new SubFlowActionNodeAdd(dropPoint, nodeToAdd, graphics, observer);
    }
}
