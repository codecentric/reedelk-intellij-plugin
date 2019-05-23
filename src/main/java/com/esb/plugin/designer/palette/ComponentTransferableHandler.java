package com.esb.plugin.designer.palette;

import com.esb.plugin.component.domain.ComponentDescriptor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.Transferable;

public class ComponentTransferableHandler extends TransferHandler {

    public int getSourceActions(JComponent Source) {
        return COPY_OR_MOVE;
    }

    protected Transferable createTransferable(JComponent source) {
        JTree tree = (JTree) source;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) return null;

        ComponentDescriptor descriptor = (ComponentDescriptor) node.getUserObject();
        return new ComponentTransferable(descriptor);
    }

}
