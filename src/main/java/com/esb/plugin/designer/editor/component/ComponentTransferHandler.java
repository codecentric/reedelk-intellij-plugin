package com.esb.plugin.designer.editor.component;

import com.intellij.openapi.util.Pair;

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
        Pair<String, String> userObject = (Pair<String, String>) node.getUserObject();

        String componentFullyQualifiedName = userObject.second;
        return new TransferableComponent(componentFullyQualifiedName);
    }

}
