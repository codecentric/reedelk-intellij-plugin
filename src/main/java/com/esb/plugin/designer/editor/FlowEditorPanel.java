package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.esb.plugin.designer.graph.DropListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ThreeComponentsSplitter;

import java.awt.dnd.DropTarget;
import java.util.TooManyListenersException;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;


public class FlowEditorPanel extends ThreeComponentsSplitter implements FlowGraphChangeListener {

    private static final Logger LOG = Logger.getInstance(FlowEditorPanel.class);

    private static final int DIVIDER_WIDTH = 2;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    private static final boolean VERTICAL = true;
    private final PropertiesPanel propertiesPanel;
    private PalettePanel palette;
    private DesignerPanel designer;

    FlowEditorPanel() {
        super(VERTICAL);

        this.propertiesPanel = new PropertiesPanel();
        this.designer = new DesignerPanel();
        registerDropTargetListener(this.designer);
        this.designer.addSelectListener(propertiesPanel);

        this.palette = new PalettePanel();


        ScrollableDesignerPanel designerScrollable = new ScrollableDesignerPanel(designer);
        designerScrollable.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        designerScrollable.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        ThreeComponentsSplitter paletteAndDesigner = new ThreeComponentsSplitter();
        paletteAndDesigner.setDividerWidth(DIVIDER_WIDTH);
        paletteAndDesigner.setInnerComponent(designerScrollable);
        paletteAndDesigner.setLastComponent(palette);
        paletteAndDesigner.setLastSize(PALETTE_SIZE);

        setInnerComponent(paletteAndDesigner);
        setLastComponent(this.propertiesPanel);
        setLastSize(PROPERTIES_PANEL_SIZE);
    }

    @Override
    public void updated(FlowGraph graph) {
        // TODO: Should not be like this.
        //  Designer Panel should be argument of this class and
        //  set listener *BEFORE* during construction
        this.designer.updated(graph);
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
