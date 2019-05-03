package com.esb.plugin.designer.canvas.action;

import com.esb.plugin.component.ComponentDescriptor;
import com.intellij.openapi.diagnostic.Logger;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.util.Optional;

import static java.util.Arrays.asList;

class DropActionHandlerUtils {

    private static final Logger LOG = Logger.getInstance(DropActionHandler.class);

    static Optional<ComponentDescriptor> getComponentDescriptorFrom(DropTargetDropEvent dropEvent) {
        Transferable transferable = dropEvent.getTransferable();
        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();
        if (asList(transferDataFlavor).contains(ComponentDescriptor.FLAVOR)) {
            try {
                ComponentDescriptor descriptor = (ComponentDescriptor) transferable.getTransferData(ComponentDescriptor.FLAVOR);
                return Optional.of(descriptor);
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.error("Could not extract dropped component name", e);
            }
        }
        return Optional.empty();
    }

}
