package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.DesignerPanel;
import com.esb.plugin.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.editor.palette.PalettePanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

import javax.swing.event.AncestorListener;

class CanvasAndPalettePanel extends ThreeComponentsSplitter {

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;

    CanvasAndPalettePanel(Module module, GraphSnapshot snapshot, SelectListener selectListener, AncestorListener listener) {
        DesignerPanel canvas = new DesignerPanel(module, snapshot);
        canvas.addListener(selectListener);
        ScrollableDesignerPanel canvasPanel = new ScrollableDesignerPanel(canvas);

        PalettePanel palette = new PalettePanel(module);

        setDividerWidth(DIVIDER_WIDTH);
        setInnerComponent(canvasPanel);
        setLastComponent(palette);
        setLastSize(PALETTE_SIZE);

        addAncestorListener(listener);
    }
}
