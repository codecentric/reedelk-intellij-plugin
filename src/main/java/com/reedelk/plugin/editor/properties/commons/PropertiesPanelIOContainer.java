package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.ComponentIO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.Optional;

import static com.intellij.util.ui.JBUI.Borders.customLine;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.commons.Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG;

public class PropertiesPanelIOContainer extends DisposablePanel {

    public PropertiesPanelIOContainer(Module module, String componentFullyQualifiedName) {
        super(new BorderLayout());
        add(new HeaderPanel("Component Input"), BorderLayout.NORTH);
        DisposablePanel panel = ContainerFactory.pushTop(new ContentPanel(module, componentFullyQualifiedName));
        panel.setBackground(Color.WHITE);
        add(panel, BorderLayout.CENTER);
        setBackground(Color.WHITE);
    }


    static class HeaderPanel extends DisposablePanel {
        public HeaderPanel(String text) {
            super(new BorderLayout());
            setBackground(SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);

            JLabel label = new JLabel(text, SwingConstants.LEFT);
            //label.setFont(label.getFont().deriveFont(Font, label.getFont().getSize()));
            label.setForeground(Color.DARK_GRAY);
            add(label, BorderLayout.WEST);
            setPreferredSize(new Dimension(200, 25));

            Border inside = emptyLeft(10);
            Border outside = customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
            setBorder(new CompoundBorder(outside, inside));
        }
    }

    static class ContentPanel extends DisposablePanel {
        public ContentPanel(Module module, String componentFullyQualifiedName) {
            super(new GridBagLayout());
            setBackground(Color.WHITE);

            Optional<ComponentIO> componentIO = CompletionService.getInstance(module).componentIOOf(componentFullyQualifiedName);

            FormBuilder.get().addLabel("Message", this);
            FormBuilder.get().addLastField(new JLabel(), this);
            FormBuilder.get().addLabel("Attributes", this);
            FormBuilder.get().addLastField(new JLabel(), this);
            FormBuilder.get().addLabel("Payload", this);
            FormBuilder.get().addLastField(new JLabel(), this);
        }
    }
}
