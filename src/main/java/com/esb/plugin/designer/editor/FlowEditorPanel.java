package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.openapi.vfs.VirtualFile;

import java.net.MalformedURLException;
import java.net.URL;


public class FlowEditorPanel extends ThreeComponentsSplitter {

    private static final int DIVIDER_WIDTH = 2;
    private static final boolean VERTICAL = true;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    public FlowEditorPanel(Project project, VirtualFile file) {
        super(VERTICAL);

        FlowGraph graph = buildGraphFromFile(file);

        PalettePanel palettePanel = new PalettePanel();
        DesignerPanel designerPanel = new DesignerPanel(graph);
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

    private FlowGraph buildGraphFromFile(VirtualFile file) {
        try {
            String json = FileUtils.readFrom(new URL(file.getUrl()));
            FlowGraphBuilder builder = new FlowGraphBuilder(json);
            return builder.get();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
