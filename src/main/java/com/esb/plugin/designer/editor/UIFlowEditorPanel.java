package com.esb.plugin.designer.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

public class UIFlowEditorPanel extends ThreeComponentsSplitter {

    private static final boolean VERTICAL = true;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    public UIFlowEditorPanel(Project project) {
        super(VERTICAL);

        PalettePanel palettePanel = new PalettePanel();
        FlowDesignerPanel flowDesignerPanel = new FlowDesignerPanel();

        ThreeComponentsSplitter horizontalSplitter = new ThreeComponentsSplitter();
        horizontalSplitter.setInnerComponent(flowDesignerPanel);
        horizontalSplitter.setLastComponent(palettePanel);
        horizontalSplitter.setLastSize(PALETTE_SIZE);
        horizontalSplitter.setDividerWidth(2);


        setInnerComponent(horizontalSplitter);
        setLastComponent(new PropertiesPanel());
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

}
