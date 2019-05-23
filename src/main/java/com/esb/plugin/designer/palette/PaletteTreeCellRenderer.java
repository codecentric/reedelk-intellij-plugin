package com.esb.plugin.designer.palette;


import com.esb.plugin.component.domain.ComponentDescriptor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class PaletteTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object componentDescriptor = node.getUserObject();
        if (componentDescriptor instanceof ComponentDescriptor) {
            ComponentDescriptor descriptor = (ComponentDescriptor) componentDescriptor;
            setText(descriptor.getDisplayName());
            setIcon(descriptor.getIcon());
        }
        return this;
    }
}
