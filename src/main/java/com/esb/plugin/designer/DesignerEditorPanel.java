package com.esb.plugin.designer;

import com.esb.plugin.designer.properties.ScrollablePropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

import javax.swing.event.AncestorListener;

public class DesignerEditorPanel extends ThreeComponentsSplitter {

    private static final int PROPERTIES_PANEL_SIZE = 200;
    private static final boolean VERTICAL = true;

    DesignerEditorPanel(Project project, Module module, GraphSnapshot snapshot, AncestorListener listener) {
        super(VERTICAL);

        ScrollablePropertiesPanel propertiesPanel = new ScrollablePropertiesPanel();
        CanvasAndPalettePanel canvasAndPalettePanel = new CanvasAndPalettePanel(module, snapshot, propertiesPanel, listener);

        setInnerComponent(canvasAndPalettePanel);
        setLastComponent(propertiesPanel);
        setLastSize(PROPERTIES_PANEL_SIZE);
    }
}
