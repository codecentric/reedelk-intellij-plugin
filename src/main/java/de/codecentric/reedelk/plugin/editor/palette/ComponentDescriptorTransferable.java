package de.codecentric.reedelk.plugin.editor.palette;

import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ComponentDescriptorTransferable implements Transferable {

    public static final DataFlavor FLAVOR = new DataFlavor(PaletteComponent.class,
            "Palette Component");

    private static final DataFlavor[] flavors = new DataFlavor[]{FLAVOR};

    private final PaletteComponent paletteComponent;

    public ComponentDescriptorTransferable(PaletteComponent paletteComponent) {
        this.paletteComponent = paletteComponent;
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
    public PaletteComponent getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (FLAVOR.equals(flavor)) {
            return paletteComponent;
        }
        throw new UnsupportedFlavorException(flavor);
    }
}