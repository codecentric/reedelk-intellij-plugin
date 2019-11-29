package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;

import java.awt.*;

import static com.intellij.icons.AllIcons.General.BalloonInformation;
import static com.reedelk.plugin.commons.Messages.Script;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.SwingConstants.LEFT;

class ScriptEditorDefault extends ScriptEditor {

    private static final int DIVIDER_WIDTH = 0;
    private static final int DIVIDER_MOUSE_ZONE_WIDTH = 4;
    private static final int EDITOR_CONTEXT_VARIABLES_SIZE = 170;

    private static final Dimension DEFAULT_SCRIPT_DIMENSION = new Dimension(800, 400);
    private static final boolean HORIZONTAL = false;

    ScriptEditorDefault(Module module, Document document) {
        super(module, document);
        ScriptEditor editor = new ScriptEditor(module, document);

        ThreeComponentsSplitter splitter = new ThreeComponentsSplitter(HORIZONTAL);
        splitter.setFirstComponent(new ScriptEditorContextPanel());
        splitter.setLastComponent(editor);
        splitter.setDividerWidth(DIVIDER_WIDTH);
        splitter.setFirstSize(EDITOR_CONTEXT_VARIABLES_SIZE);
        splitter.setDividerMouseZoneSize(DIVIDER_MOUSE_ZONE_WIDTH);

        setPreferredSize(DEFAULT_SCRIPT_DIMENSION);
        setLayout(new BorderLayout());
        add(splitter, CENTER);

        JBLabel warn = new JBLabel(Script.DIALOG_EDIT_WARNING_RETURN_KEYWORD.format(), BalloonInformation, LEFT);
        add(warn, SOUTH);
    }
}
