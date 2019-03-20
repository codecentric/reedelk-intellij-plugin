package com.esb.plugin.designer.editor.component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.Transferable;

public class ComponentTransferHandler extends TransferHandler {

    public int getSourceActions(JComponent Source) {
        return COPY_OR_MOVE;
    }

    protected Transferable createTransferable(JComponent source) {
        JTree tree = (JTree) source;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) return null;
        String userObject = (String) node.getUserObject();

        return new TransferableComponent(userObject);
    }

}
