package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;


public class FlowEditorPanel extends ThreeComponentsSplitter {

    private static final boolean VERTICAL = true;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    public FlowEditorPanel(Project project) {
        super(VERTICAL);

        PalettePanel palettePanel = new PalettePanel();
        DesignerPanel designerPanel = new DesignerPanel();
        designerPanel.setPreferredSize(JBUI.size(400, 400));

        JBScrollPane scrollableDesignerPanel = new JBScrollPane(designerPanel);
        scrollableDesignerPanel.createHorizontalScrollBar();
        scrollableDesignerPanel.createVerticalScrollBar();
        scrollableDesignerPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollableDesignerPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        ThreeComponentsSplitter horizontalSplitter = new ThreeComponentsSplitter();
        horizontalSplitter.setInnerComponent(scrollableDesignerPanel);
        horizontalSplitter.setLastComponent(palettePanel);
        horizontalSplitter.setLastSize(PALETTE_SIZE);
        horizontalSplitter.setDividerWidth(2);


        setInnerComponent(horizontalSplitter);
        setLastComponent(new PropertiesPanel());
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

}
