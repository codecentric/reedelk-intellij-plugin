package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableThreeComponentsSplitter;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;

import java.awt.*;

import static com.intellij.icons.AllIcons.General.BalloonInformation;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.SwingConstants.LEFT;

class ScriptEditorDefault extends DisposablePanel {

    private static final int DIVIDER_WIDTH = 0;
    private static final int DIVIDER_MOUSE_ZONE_WIDTH = 4;
    private static final int EDITOR_CONTEXT_VARIABLES_SIZE = 190;

    static final Dimension DEFAULT_SCRIPT_DIMENSION = new Dimension(800, 400);
    private static final boolean HORIZONTAL = false;

    ScriptEditorDefault(Module module, Document document, ContainerContext context) {
        ScriptEditor editor = new ScriptEditor(module, document, context);

        ThreeComponentsSplitter splitter = new DisposableThreeComponentsSplitter(HORIZONTAL);
        splitter.setFirstComponent(new ScriptEditorContextPanel(module, context.componentFullyQualifiedName()));
        splitter.setLastComponent(editor);
        splitter.setDividerWidth(DIVIDER_WIDTH);
        splitter.setFirstSize(EDITOR_CONTEXT_VARIABLES_SIZE);
        splitter.setDividerMouseZoneSize(DIVIDER_MOUSE_ZONE_WIDTH);

        setPreferredSize(DEFAULT_SCRIPT_DIMENSION);
        setLayout(new BorderLayout());
        add(splitter, CENTER);

        JBLabel warn = new JBLabel(message("script.dialog.edit.warning.return.keyword"), BalloonInformation, LEFT);
        add(warn, SOUTH);
    }
}
