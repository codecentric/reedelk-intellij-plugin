package com.esb.plugin.designer.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;


public class UIFlowEditorPanel extends ThreeComponentsSplitter {

    private static final boolean VERTICAL = true;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    public UIFlowEditorPanel(Project project) {
        super(VERTICAL);

        PalettePanel palettePanel = new PalettePanel();
        FlowDesignerPanel flowDesignerPanel = new FlowDesignerPanel();
        flowDesignerPanel.setPreferredSize(JBUI.size(400, 400));

        JBScrollPane jScrollPane = new JBScrollPane(flowDesignerPanel);
        jScrollPane.createHorizontalScrollBar();
        jScrollPane.createVerticalScrollBar();
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        ThreeComponentsSplitter horizontalSplitter = new ThreeComponentsSplitter();
        horizontalSplitter.setInnerComponent(jScrollPane);
        horizontalSplitter.setLastComponent(palettePanel);
        horizontalSplitter.setLastSize(PALETTE_SIZE);
        horizontalSplitter.setDividerWidth(2);


        setInnerComponent(horizontalSplitter);
        setLastComponent(new PropertiesPanel());
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

}
