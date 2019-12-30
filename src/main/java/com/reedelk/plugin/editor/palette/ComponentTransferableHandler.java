package com.reedelk.plugin.editor.palette;

import com.intellij.openapi.util.SystemInfo;
import com.reedelk.module.descriptor.model.ComponentDescriptor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.datatransfer.Transferable;

import static com.reedelk.plugin.editor.designer.icon.Icon.Dimension.HALF_ICON_HEIGHT;
import static com.reedelk.plugin.editor.designer.icon.Icon.Dimension.HALF_ICON_WIDTH;

public class ComponentTransferableHandler extends TransferHandler {

    @Override
    public int getSourceActions(JComponent source) {
        return COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent source) {

        JTree tree = (JTree) source;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        Object userObject = node.getUserObject();

        if (userObject instanceof ComponentDescriptor) {

            ComponentDescriptor descriptor = (ComponentDescriptor) userObject;

            setDragImage(descriptor);

            return new ComponentDescriptorTransferable(descriptor);

        } else {

            return new EmptyTransferable();

        }
    }

    private void setDragImage(ComponentDescriptor descriptor) {
        setDragImage(descriptor.getImage());
        if (SystemInfo.isMac) {
            // On Mac the offset must be negative.
            setDragImageOffset(new Point(-HALF_ICON_WIDTH, -HALF_ICON_HEIGHT));
        } else {
            setDragImageOffset(new Point(HALF_ICON_WIDTH, HALF_ICON_HEIGHT));
        }
    }
}
