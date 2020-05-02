package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.icons.AllIcons.General.BalloonInformation;
import static com.reedelk.plugin.commons.Colors.SCRIPT_EDITOR_DIALOG_BACKGROUND;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static javax.swing.SwingConstants.LEFT;

class ScriptEditorDefault extends DisposablePanel {

    private static final int DIVIDER_WIDTH = 0;
    private static final int DIVIDER_MOUSE_ZONE_WIDTH = 4;
    private static final int EDITOR_CONTEXT_VARIABLES_SIZE = 180;

    static final Dimension DEFAULT_SCRIPT_DIMENSION = new Dimension(800, 600);
    private static final boolean HORIZONTAL = false;

    ScriptEditorDefault(@NotNull Module module,
                        @NotNull Document document,
                        @NotNull ContainerContext context) {

        ScriptEditor editor = new ScriptEditor(module, document, context);

        // This panel forces the background color of the editor to not pick up the
        // bg color of the ThreeComponentsSplitter.
        DisposablePanel editorWrapperContainer = ContainerFactory.pushCenter(editor);
        editorWrapperContainer.setBackground(SCRIPT_EDITOR_DIALOG_BACKGROUND);

        ThreeComponentsSplitter splitter = new DisposableThreeComponentsSplitter(HORIZONTAL);
        splitter.setFirstComponent(new ScriptEditorContextPanel(module, context.componentPropertyPath()));
        splitter.setLastComponent(editorWrapperContainer);
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
