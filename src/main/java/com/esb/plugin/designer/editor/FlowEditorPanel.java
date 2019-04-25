package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;

import java.awt.dnd.DropTarget;
import java.util.TooManyListenersException;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;


public class FlowEditorPanel extends ThreeComponentsSplitter {

    private static final Logger LOG = Logger.getInstance(FlowEditorPanel.class);

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 150;

    private static final boolean VERTICAL = true;

    private final PropertiesPanel propertiesPanel;
    private PalettePanel palette;
    private DesignerPanel designer;

    FlowEditorPanel(Module module, VirtualFile file) {
        super(VERTICAL);

        this.propertiesPanel = new PropertiesPanel();
        JBScrollPane propertiesPanelScrollable = new JBScrollPane(this.propertiesPanel);
        propertiesPanelScrollable.createVerticalScrollBar();
        propertiesPanelScrollable.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        this.designer = new DesignerPanel(module, file);
        registerDropTargetListener(this.designer);

        this.palette = new PalettePanel(module, file);

        this.designer.addSelectListener(propertiesPanel);
        ScrollableDesignerPanel designerScrollable = new ScrollableDesignerPanel(designer);
        designerScrollable.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        designerScrollable.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        ThreeComponentsSplitter paletteAndDesigner = new ThreeComponentsSplitter();
        paletteAndDesigner.setDividerWidth(DIVIDER_WIDTH);
        paletteAndDesigner.setInnerComponent(designerScrollable);
        paletteAndDesigner.setLastComponent(palette);
        paletteAndDesigner.setLastSize(PALETTE_SIZE);

        setInnerComponent(paletteAndDesigner);
        setLastComponent(propertiesPanelScrollable);
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

    private void registerDropTargetListener(DesignerPanel designer) {
        DropTarget dropTarget = new DropTarget();
        designer.setDropTarget(dropTarget);
        try {
            dropTarget.addDropTargetListener(designer);
        } catch (TooManyListenersException e) {
            LOG.error("DropTarget listener error", e);
        }
    }
}
