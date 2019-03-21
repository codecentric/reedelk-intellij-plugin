package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.editor.common.FlowDataStructure;
import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;


public class FlowEditorPanel extends ThreeComponentsSplitter {

    private static final int DIVIDER_WIDTH = 2;
    private static final boolean VERTICAL = true;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    public FlowEditorPanel(Project project) {
        super(VERTICAL);

        PalettePanel palettePanel = new PalettePanel();

        FlowDataStructure flowDataStructure = new FlowDataStructure();
        DesignerPanel designerPanel = new DesignerPanel(flowDataStructure);
        ScrollableDesignerPanel scrollableDesignerPanel = new ScrollableDesignerPanel(designerPanel);

        ThreeComponentsSplitter paletteAndDesignerSplitter = new ThreeComponentsSplitter();
        paletteAndDesignerSplitter.setInnerComponent(scrollableDesignerPanel);
        paletteAndDesignerSplitter.setLastComponent(palettePanel);
        paletteAndDesignerSplitter.setLastSize(PALETTE_SIZE);
        paletteAndDesignerSplitter.setDividerWidth(DIVIDER_WIDTH);

        setInnerComponent(paletteAndDesignerSplitter);
        setLastComponent(new PropertiesPanel());
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

}
