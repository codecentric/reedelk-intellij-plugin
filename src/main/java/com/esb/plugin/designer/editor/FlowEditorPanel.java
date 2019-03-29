package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.esb.plugin.designer.graph.FlowGraph;
import com.intellij.openapi.ui.ThreeComponentsSplitter;


public class FlowEditorPanel extends ThreeComponentsSplitter implements GraphChangeListener {

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    private static final boolean VERTICAL = true;
    private PalettePanel palette;
    private DesignerPanel designer;

    FlowEditorPanel() {
        super(VERTICAL);

        this.palette = new PalettePanel();
        this.designer = new DesignerPanel();

        ThreeComponentsSplitter paletteAndDesigner = new ThreeComponentsSplitter();
        ScrollableDesignerPanel designerScrollable = new ScrollableDesignerPanel(designer);
        paletteAndDesigner.setDividerWidth(DIVIDER_WIDTH);
        paletteAndDesigner.setInnerComponent(designerScrollable);
        paletteAndDesigner.setLastComponent(palette);
        paletteAndDesigner.setLastSize(PALETTE_SIZE);

        setInnerComponent(paletteAndDesigner);
        setLastComponent(new PropertiesPanel());
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

    @Override
    public void updated(FlowGraph graph) {
        this.designer.updated(graph);
    }
}
