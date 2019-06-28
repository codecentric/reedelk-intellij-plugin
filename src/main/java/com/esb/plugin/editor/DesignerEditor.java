package com.esb.plugin.editor;


import com.esb.plugin.editor.designer.DesignerPanel;
import com.esb.plugin.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.editor.properties.PropertiesPanel;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

public class DesignerEditor extends ThreeComponentsSplitter {

    private static final int PROPERTIES_PANEL_SIZE = 170;
    private static final boolean VERTICAL = true;

    DesignerEditor(DesignerPanel designerPanel, PropertiesPanel propertiesPanel) {
        super(VERTICAL);
        ScrollableDesignerPanel canvasPanel = new ScrollableDesignerPanel(designerPanel);

        setInnerComponent(canvasPanel);
        setLastComponent(propertiesPanel);
        setLastSize(PROPERTIES_PANEL_SIZE);
    }
}
