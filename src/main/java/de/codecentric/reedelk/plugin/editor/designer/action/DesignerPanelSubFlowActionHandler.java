package de.codecentric.reedelk.plugin.editor.designer.action;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.editor.designer.action.add.SubFlowActionNodeAdd;
import de.codecentric.reedelk.plugin.editor.designer.action.move.SubFlowActionNodeReplace;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.SubFlowActionNodeRemove;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class DesignerPanelSubFlowActionHandler extends DesignerPanelAbstractActionHandler {

    public DesignerPanelSubFlowActionHandler(@NotNull Module module, @NotNull FlowSnapshot snapshot) {
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
