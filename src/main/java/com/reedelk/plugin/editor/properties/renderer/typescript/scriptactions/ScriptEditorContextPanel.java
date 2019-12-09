package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

class ScriptEditorContextPanel extends DisposablePanel {

    private static final Border MATTE_BORDER = BorderFactory.createMatteBorder(1, 1, 1, 0,
            Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER);
    private static final Border COMPOUND_BORDER = new CompoundBorder(
            JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER_BOTTOM, 0, 0, 1, 0),
            JBUI.Borders.empty(5));

    ScriptEditorContextPanel() {
        setLayout(new BorderLayout());
        setBorder(MATTE_BORDER);

        JLabel panelTitle = new JLabel(message("script.editor.context.vars.title"));
        JPanel panelTitleWrapper = new JPanel();
        panelTitleWrapper.setBackground(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);
        panelTitleWrapper.setBorder(COMPOUND_BORDER);
        panelTitleWrapper.setLayout(new BorderLayout());
        panelTitleWrapper.add(panelTitle, NORTH);
        add(panelTitleWrapper, NORTH);


        // TODO: Should not be hardcoded the context variables!
        JPanel panelVariablesWrapper = new DisposablePanel();
        BoxLayout boxLayout = new BoxLayout(panelVariablesWrapper, BoxLayout.PAGE_AXIS);
        panelVariablesWrapper.setLayout(boxLayout);
        panelVariablesWrapper.setBorder(JBUI.Borders.empty(5));
        panelVariablesWrapper.add(new ContextVariableLabel("message", "Message"));
        panelVariablesWrapper.add(new ContextVariableLabel("context", "Context"));

        JBScrollPane panelVariablesScrollPane = new JBScrollPane(panelVariablesWrapper);
        panelVariablesScrollPane.setBorder(JBUI.Borders.empty());
        add(panelVariablesScrollPane, CENTER);
    }

    static class ContextVariableLabel extends JLabel {
        static final String template = "<html>%s: %s</html>"; // TODO: Use Reedelk Bundle

        ContextVariableLabel(String name, String type) {
            super(String.format(template, name, type));
            setBorder(JBUI.Borders.emptyTop(4));
        }
    }
}
