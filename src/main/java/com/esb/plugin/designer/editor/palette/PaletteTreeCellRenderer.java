package com.esb.plugin.designer.editor.palette;


import com.esb.plugin.commons.ESBIcons;
import com.intellij.openapi.util.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class PaletteTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        if (userObject instanceof Pair) {
            Pair<String, String> displayAndFullyQualifiedName = (Pair<String, String>) userObject;
            setText(displayAndFullyQualifiedName.first);
            setIcon(ESBIcons.forComponentAsIcon(displayAndFullyQualifiedName.second));
        }

        return this;
    }
}
