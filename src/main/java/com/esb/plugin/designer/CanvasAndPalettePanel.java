package com.esb.plugin.designer;

import com.esb.plugin.designer.canvas.CanvasPanel;
import com.esb.plugin.designer.canvas.ScrollableCanvasPanel;
import com.esb.plugin.designer.palette.PalettePanel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.openapi.vfs.VirtualFile;

class CanvasAndPalettePanel extends ThreeComponentsSplitter {

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;

    private final PalettePanel palette;
    private final ScrollableCanvasPanel canvas;

    CanvasAndPalettePanel(Module module, VirtualFile file, SelectListener selectListener) {
        canvas = createCanvas(module, file, selectListener);
        palette = createPalette(module, file);
        setDividerWidth(DIVIDER_WIDTH);
        setInnerComponent(canvas);
        setLastComponent(palette);
        setLastSize(PALETTE_SIZE);
    }

    private ScrollableCanvasPanel createCanvas(Module module, VirtualFile file, SelectListener listener) {
        CanvasPanel canvas = new CanvasPanel(module, file);
        canvas.addSelectListener(listener);
        return new ScrollableCanvasPanel(canvas);
    }

    private PalettePanel createPalette(Module module, VirtualFile file) {
        return new PalettePanel(module, file);
    }
}
