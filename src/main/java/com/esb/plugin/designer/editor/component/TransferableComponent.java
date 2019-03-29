package com.esb.plugin.designer.editor.component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableComponent implements Transferable {

    private final DataFlavor[] flavors;
    private String componentFullyQualifiedName;

    public TransferableComponent(String componentFullyQualifiedName) {
        this.flavors = new DataFlavor[]{DataFlavor.stringFlavor};
        this.componentFullyQualifiedName = componentFullyQualifiedName;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.stringFlavor.equals(flavor);
    }

    @Override
    public String getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return componentFullyQualifiedName;
    }

}
