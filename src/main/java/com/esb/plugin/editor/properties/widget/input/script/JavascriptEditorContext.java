package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Labels;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

class JavascriptEditorContext extends JPanel {

    JavascriptEditorContext() {
        setLayout(new BorderLayout());
        Border border = BorderFactory.createMatteBorder(1, 1, 1, 0, JBColor.LIGHT_GRAY);
        setBorder(border);

        JLabel label = new JLabel(Labels.SCRIPT_EDITOR_CONTEXT_VARS);
        JPanel labelWrapper = new JPanel();
        labelWrapper.setBackground(new Color(226, 230, 236));

        Border bottom = JBUI.Borders.customLine(new Color(201, 201, 201), 0, 0, 1, 0);
        Border padding = JBUI.Borders.empty(5);
        labelWrapper.setBorder(new CompoundBorder(bottom, padding));
        labelWrapper.setLayout(new BorderLayout());
        labelWrapper.add(label, BorderLayout.NORTH);
        add(labelWrapper, BorderLayout.NORTH);


        JPanel context = new JPanel();
        BoxLayout boxLayout = new BoxLayout(context, BoxLayout.PAGE_AXIS);
        context.setLayout(boxLayout);
        context.setBorder(JBUI.Borders.empty(5));
        DEFAULT_VARIABLES.forEach(context::add);

        JBScrollPane scrollPane = new JBScrollPane(context);
        scrollPane.setBorder(JBUI.Borders.empty());
        add(scrollPane, BorderLayout.CENTER);
    }

    class ContextVariable extends JLabel {
        static final String template = "<html><i>%s</i>: %s</html>";

        ContextVariable(String name, String type) {
            super(String.format(template, name, type));
            setBorder(JBUI.Borders.emptyTop(4));
        }
    }

    private final List<ContextVariable> DEFAULT_VARIABLES = Arrays.asList(
            new ContextVariable("message", "Message"),
            new ContextVariable("payload", "Object"),
            new ContextVariable("inboundProperties", "Map"),
            new ContextVariable("outboundProperties", "Map"));
}
