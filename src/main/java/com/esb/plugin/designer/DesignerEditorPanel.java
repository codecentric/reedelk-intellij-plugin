package com.esb.plugin.designer;

import com.esb.plugin.designer.properties.PropertiesPanel;
import com.esb.plugin.designer.properties.ScrollablePropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

public class DesignerEditorPanel extends ThreeComponentsSplitter {

    private static final int PROPERTIES_PANEL_SIZE = 190;

    private static final boolean VERTICAL = true;

    private final ScrollablePropertiesPanel properties;

    private CanvasAndPalettePanel canvasAndPalettePanel;

    DesignerEditorPanel(Module module, GraphSnapshot snapshot) {
        super(VERTICAL);

        PropertiesPanel panel = new PropertiesPanel();
        properties = new ScrollablePropertiesPanel(panel);

        canvasAndPalettePanel = new CanvasAndPalettePanel(module, snapshot, properties);

        setInnerComponent(canvasAndPalettePanel);
        setLastComponent(properties);
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

}
