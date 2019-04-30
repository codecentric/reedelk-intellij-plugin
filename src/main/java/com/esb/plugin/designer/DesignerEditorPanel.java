package com.esb.plugin.designer;

import com.esb.plugin.designer.properties.PropertiesPanel;
import com.esb.plugin.designer.properties.ScrollablePropertiesPanel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.openapi.vfs.VirtualFile;

public class DesignerEditorPanel extends ThreeComponentsSplitter {

    private static final int PROPERTIES_PANEL_SIZE = 150;

    private static final boolean VERTICAL = true;

    private final ScrollablePropertiesPanel properties;

    DesignerEditorPanel(Module module, VirtualFile file) {
        super(VERTICAL);

        properties = createProperties(module, file);

        CanvasAndPalettePanel canvasAndPalettePanel = new CanvasAndPalettePanel(module, file, properties);

        setInnerComponent(canvasAndPalettePanel);
        setLastComponent(properties);
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

    private ScrollablePropertiesPanel createProperties(Module module, VirtualFile file) {
        PropertiesPanel properties = new PropertiesPanel(module, file);
        return new ScrollablePropertiesPanel(properties);
    }

}
