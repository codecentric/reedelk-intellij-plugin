package com.reedelk.plugin.editor.palette;

import com.reedelk.component.descriptor.ComponentDescriptor;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ComponentDescriptorTransferable implements Transferable {

    public static final DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class, "Descriptor of a component");

    private static final DataFlavor[] flavors = new DataFlavor[]{FLAVOR};

    private final ComponentDescriptor descriptor;

    public ComponentDescriptorTransferable(ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return FLAVOR.equals(flavor);
    }

    @Override
    @NotNull
    public ComponentDescriptor getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (FLAVOR.equals(flavor)) {
            return descriptor;
        }
        throw new UnsupportedFlavorException(flavor);
    }
}