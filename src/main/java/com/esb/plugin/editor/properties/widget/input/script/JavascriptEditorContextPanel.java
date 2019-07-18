package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.commons.Labels;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.Set;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

class JavascriptEditorContextPanel extends JPanel {

    private final Border panelBorder = BorderFactory.createMatteBorder(1, 1, 1, 0,
            Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER);

    private final Border panelTitleBorder = new CompoundBorder(
            JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER_BOTTOM, 0, 0, 1, 0),
            JBUI.Borders.empty(5));

    JavascriptEditorContextPanel(Set<ScriptContextManager.ContextVariable> contextVariables) {
        setLayout(new BorderLayout());
        setBorder(panelBorder);

        JLabel panelTitle = new JLabel(Labels.SCRIPT_EDITOR_CONTEXT_VARS_TITLE);
        JPanel panelTitleWrapper = new JPanel();
        panelTitleWrapper.setBackground(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);
        panelTitleWrapper.setBorder(panelTitleBorder);
        panelTitleWrapper.setLayout(new BorderLayout());
        panelTitleWrapper.add(panelTitle, NORTH);
        add(panelTitleWrapper, NORTH);


        JPanel panelVariablesWrapper = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panelVariablesWrapper, BoxLayout.PAGE_AXIS);
        panelVariablesWrapper.setLayout(boxLayout);
        panelVariablesWrapper.setBorder(JBUI.Borders.empty(5));
        contextVariables
                .stream()
                .map(contextVariable -> new ContextVariableLabel(contextVariable.name, contextVariable.type))
                .forEach(panelVariablesWrapper::add);

        JBScrollPane panelVariablesScrollPane = new JBScrollPane(panelVariablesWrapper);
        panelVariablesScrollPane.setBorder(JBUI.Borders.empty());
        add(panelVariablesScrollPane, CENTER);
    }

    static class ContextVariableLabel extends JLabel {
        static final String template = "<html><i>%s</i>: %s</html>";

        ContextVariableLabel(String name, String type) {
            super(String.format(template, name, type));
            setBorder(JBUI.Borders.emptyTop(4));
        }
    }
}
