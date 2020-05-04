package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.ComponentIO;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

import static com.intellij.icons.AllIcons.General.ArrowRight;
import static com.intellij.util.ui.JBUI.Borders.customLine;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.commons.Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG;

public class PropertiesPanelIOContainer extends DisposablePanel {

    public PropertiesPanelIOContainer(Module module, String componentFullyQualifiedName) {
        super(new BorderLayout());
        add(new HeaderPanel("Input Message"), BorderLayout.NORTH);
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
            setBorder(JBUI.Borders.empty(5, 1, 5, 8));
            setBackground(Color.WHITE);

            Optional<ComponentIO> componentIO = CompletionService.getInstance(module).componentIOOf(componentFullyQualifiedName);


            TypeObjectContainerHeader header =
                    new TypeObjectContainerHeader("attributes", ArrowRight, () -> System.out.println("Clicked"));
            FormBuilder.get().addFullWidthAndHeight(header, ContentPanel.this);

            componentIO.ifPresent(new Consumer<ComponentIO>() {
                @Override
                public void accept(ComponentIO componentIO) {

                    componentIO.getOutputAttributes().forEach(new Consumer<Suggestion>() {
                        @Override
                        public void accept(Suggestion suggestion) {
                            String label = suggestion.lookupString() + ": " + suggestion.presentableType();
                            JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                            attributes.setBorder(JBUI.Borders.emptyLeft(30));
                            FormBuilder.get().addLabel(attributes, ContentPanel.this);
                            FormBuilder.get().addLastField(Box.createHorizontalGlue(), ContentPanel.this);
                        }
                    });
                }
            });


            TypeObjectContainerHeader payload =
                    new TypeObjectContainerHeader("payload", ArrowRight, () -> System.out.println("Clicked"));
            payload.setBorder(JBUI.Borders.emptyTop(5));
            FormBuilder.get().addFullWidthAndHeight(payload, ContentPanel.this);
        }
    }
}
