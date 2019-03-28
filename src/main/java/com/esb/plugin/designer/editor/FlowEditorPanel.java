package com.esb.plugin.designer.editor;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.editor.designer.DesignerPanel;
import com.esb.plugin.designer.editor.designer.ScrollableDesignerPanel;
import com.esb.plugin.designer.editor.palette.PalettePanel;
import com.esb.plugin.designer.editor.properties.PropertiesPanel;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.builder.FlowGraphBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;


public class FlowEditorPanel extends ThreeComponentsSplitter {

    private static final int DIVIDER_WIDTH = 2;
    private static final boolean VERTICAL = true;
    private static final int PALETTE_SIZE = 210;
    private static final int PROPERTIES_PANEL_SIZE = 100;

    public FlowEditorPanel(Project project, VirtualFile editorFile) {
        super(VERTICAL);

        FlowGraph graph = buildGraphFromFile(editorFile);


        PalettePanel palettePanel = new PalettePanel();
        DesignerPanel designerPanel = new DesignerPanel(graph, project);


        FileEditorManager.getInstance(project).addFileEditorManagerListener(new FileEditorManagerListener() {
            @Override
            public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull Pair<FileEditor[], FileEditorProvider[]> editors) {
                if (file.getUrl().equals(editorFile.getUrl())) {
                    Optional<FileEditor> editor = Arrays.stream(editors.first).filter(fileEditor -> fileEditor instanceof PsiAwareTextEditorImpl).findFirst();
                    editor.ifPresent(new Consumer<FileEditor>() {
                        @Override
                        public void accept(FileEditor fileEditor) {
                            TextEditor edt = (TextEditor) fileEditor;
                            Document document = edt.getEditor().getDocument();
                            document.addDocumentListener(new DocumentListener() {
                                @Override
                                public void documentChanged(@NotNull DocumentEvent event) {
                                    Document document1 = event.getDocument();
                                    String json = document1.getText();
                                    designerPanel.modifiedJson(json);
                                }
                            });
                        }
                    });
                }

            }
        });

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
            return builder.graph();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
