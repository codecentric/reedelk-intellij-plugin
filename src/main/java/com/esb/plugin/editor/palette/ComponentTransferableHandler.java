package com.esb.plugin.editor.palette;

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

        Object userObject = node.getUserObject();

        if (userObject instanceof ComponentDescriptor) {

            ComponentDescriptor descriptor = (ComponentDescriptor) userObject;

            return new ComponentDescriptorTransferable(descriptor);

        } else {

            return new EmptyTransferable();

        }
    }
}
