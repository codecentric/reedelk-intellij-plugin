package com.esb.plugin.designer;

import com.esb.plugin.designer.canvas.CanvasPanel;
import com.esb.plugin.designer.canvas.ScrollableCanvasPanel;
import com.esb.plugin.designer.palette.PalettePanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

import javax.swing.event.AncestorListener;

class CanvasAndPalettePanel extends ThreeComponentsSplitter {

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;

    CanvasAndPalettePanel(Module module, GraphSnapshot snapshot, SelectListener selectListener, AncestorListener listener) {
        CanvasPanel canvas = new CanvasPanel(module, snapshot, listener);
        canvas.addListener(selectListener);
        ScrollableCanvasPanel canvasPanel = new ScrollableCanvasPanel(canvas);

        PalettePanel palette = new PalettePanel(module);

        setDividerWidth(DIVIDER_WIDTH);
        setInnerComponent(canvasPanel);
        setLastComponent(palette);
        setLastSize(PALETTE_SIZE);
    }
}
