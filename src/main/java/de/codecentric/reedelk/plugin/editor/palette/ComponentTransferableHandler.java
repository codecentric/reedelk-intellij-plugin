package de.codecentric.reedelk.plugin.editor.palette;

import com.intellij.openapi.util.SystemInfo;
import de.codecentric.reedelk.plugin.service.module.impl.component.module.ModuleComponentDTO;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.datatransfer.Transferable;

import static de.codecentric.reedelk.plugin.editor.designer.icon.Icon.Dimension.HALF_ICON_HEIGHT;
import static de.codecentric.reedelk.plugin.editor.designer.icon.Icon.Dimension.HALF_ICON_WIDTH;

public class ComponentTransferableHandler extends TransferHandler {

    private static final Transferable EMPTY = new EmptyTransferable();

    @Override
    public int getSourceActions(JComponent source) {
        return COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent source) {
        JTree tree = (JTree) source;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node != null) {
            Object userObject = node.getUserObject();
            if (userObject instanceof ModuleComponentDTO) {
                ModuleComponentDTO descriptor = (ModuleComponentDTO) userObject;
                setDragComponentImage(descriptor.getImage());
                String fullyQualifiedName = descriptor.getFullyQualifiedName();
                PaletteComponent paletteComponent = new PaletteComponent();
                paletteComponent.setComponentFullyQualifiedName(fullyQualifiedName);
                return new ComponentDescriptorTransferable(paletteComponent);
            }
        }
        // We must reset the image, otherwise the last one is cached and used in the DnD.
        setDragImage(null);
        return EMPTY;
    }

    private void setDragComponentImage(Image dragImage) {
        setDragImage(dragImage);
        if (SystemInfo.isMac) {
            // On Mac the offset must be negative.
            setDragImageOffset(new Point(-HALF_ICON_WIDTH, -HALF_ICON_HEIGHT));
        } else {
            setDragImageOffset(new Point(HALF_ICON_WIDTH, HALF_ICON_HEIGHT));
        }
    }
}
