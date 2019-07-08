package com.esb.plugin.editor.properties.widget.input.script;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class JavascriptEditorContext extends JPanel {

    public JavascriptEditorContext() {
        setLayout(new BorderLayout());
        Border border = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.LIGHT_GRAY);
        setBorder(border);

        JLabel label = new JLabel("Context variables");
        JPanel labelWrapper = new JPanel();
        labelWrapper.setBackground(new Color(224, 224, 224));
        labelWrapper.setBorder(JBUI.Borders.empty(5));
        labelWrapper.setLayout(new BorderLayout());
        labelWrapper.add(label, BorderLayout.NORTH);
        add(labelWrapper, BorderLayout.NORTH);


        JPanel context = new JPanel();
        BoxLayout boxLayout = new BoxLayout(context, BoxLayout.PAGE_AXIS);
        context.setLayout(boxLayout);
        context.setBorder(JBUI.Borders.empty(5));
        context.add(new ContextVariable("message", "Message"));
        context.add(new ContextVariable("payload", "Object"));
        context.add(new ContextVariable("inboundProperties", "Map"));
        context.add(new ContextVariable("outboundProperties", "Map"));
        JScrollPane scrollPane = new JScrollPane(context);
        scrollPane.setBorder(JBUI.Borders.empty());
        add(scrollPane, BorderLayout.CENTER);
    }

    class ContextVariable extends JLabel {
        static final String template = "<html><i>%s</i>: %s</html>";

        ContextVariable(String name, String type) {
            super(String.format(template, name, type));
        }
    }

}
