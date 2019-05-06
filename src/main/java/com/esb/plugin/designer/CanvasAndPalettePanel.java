package com.esb.plugin.designer;

import com.esb.plugin.designer.canvas.CanvasPanel;
import com.esb.plugin.designer.canvas.ScrollableCanvasPanel;
import com.esb.plugin.designer.palette.PalettePanel;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

class CanvasAndPalettePanel extends ThreeComponentsSplitter implements GraphChangeListener {

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;

    private final PalettePanel palette;
    private final CanvasPanel canvas;

    CanvasAndPalettePanel(Module module, SelectListener selectListener) {
        canvas = new CanvasPanel(module);
        canvas.addSelectListener(selectListener);
        ScrollableCanvasPanel canvasPanel = new ScrollableCanvasPanel(canvas);

        palette = new PalettePanel(module);

        setDividerWidth(DIVIDER_WIDTH);
        setInnerComponent(canvasPanel);
        setLastComponent(palette);
        setLastSize(PALETTE_SIZE);
    }

    @Override
    public void onGraphChanged(FlowGraph updatedGraph) {
        canvas.onGraphChanged(updatedGraph);
    }

    public void addGraphChangeListener(GraphChangeListener listener) {
        this.canvas.addGraphChangeListener(listener);
    }
}
