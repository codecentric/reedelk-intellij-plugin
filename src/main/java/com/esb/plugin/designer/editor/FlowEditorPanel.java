package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.esb.plugin.designer.graph.DropListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeListener;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

import java.awt.dnd.DropTarget;


public class FlowEditorPanel extends ThreeComponentsSplitter implements FlowGraphChangeListener {

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    private static final boolean VERTICAL = true;
    private PalettePanel palette;
    private DesignerPanel designer;

    FlowEditorPanel(DropTarget dropTarget, DropListener dropListener) {
        super(VERTICAL);

        this.palette = new PalettePanel();
        this.designer = new DesignerPanel();
        this.designer.setDropTarget(dropTarget);
        this.designer.setDropListener(dropListener);

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
        // TODO: Should not be like this. Designer Panel should be argument of this class and set listener *BEFORE* during construction
        this.designer.updated(graph);
    }
}
