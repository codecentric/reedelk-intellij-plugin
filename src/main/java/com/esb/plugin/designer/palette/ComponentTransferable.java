package com.esb.plugin.designer.palette;

import com.esb.plugin.component.domain.ComponentDescriptor;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ComponentTransferable implements Transferable {

    private static final DataFlavor[] flavors = new DataFlavor[]{ComponentDescriptor.FLAVOR};

    private final ComponentDescriptor descriptor;

    public ComponentTransferable(ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return ComponentDescriptor.FLAVOR.equals(flavor);
    }

    @Override
    @NotNull
    public ComponentDescriptor getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (ComponentDescriptor.FLAVOR.equals(flavor)) {
            return descriptor;
        }
        throw new UnsupportedFlavorException(flavor);
    }

}
