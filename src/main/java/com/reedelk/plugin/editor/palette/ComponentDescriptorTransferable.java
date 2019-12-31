package com.reedelk.plugin.editor.palette;

import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ComponentDescriptorTransferable implements Transferable {

    public static final DataFlavor FLAVOR = new DataFlavor(String.class, "Fully qualified component name");

    private static final DataFlavor[] flavors = new DataFlavor[]{FLAVOR};

    private final String componentFullyQualifiedName;

    public ComponentDescriptorTransferable(String componentFullyQualifiedName) {
        this.componentFullyQualifiedName = componentFullyQualifiedName;
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
    public String getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (FLAVOR.equals(flavor)) {
            return componentFullyQualifiedName;
        }
        throw new UnsupportedFlavorException(flavor);
    }
}