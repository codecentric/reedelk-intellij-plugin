package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.TooltipContent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders;
import static java.awt.BorderLayout.*;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class ContainerFactory {

    private ContainerFactory() {
    }

    public static DisposablePanel createLabelNextToComponent(String labelText, JComponent body) {
        JBLabel label = new JBLabel(labelText);
        return createLabelNextToComponent(label, body);
    }

    public static DisposablePanel createLabelNextToComponent(JLabel label, JComponent body) {
        DisposablePanel wrapper = new DisposablePanel(new BorderLayout());
        label.setBorder(Borders.emptyRight(5));
        wrapper.add(label, WEST);
        wrapper.add(body, CENTER);
        return wrapper;
    }

    public static DisposablePanel createLabelNextToComponentWithOuterBorder(JLabel label, JComponent body) {
        return createLabelNextToComponent(label, body, true);
    }

    public static DisposablePanel createLabelNextToComponentWithoutOuterBorder(JLabel label, JComponent body) {
        return createLabelNextToComponent(label, body, false);
    }

    public static DisposablePanel createObjectTypeContainer(@NotNull JComponent renderedComponent,
                                                            @NotNull String title,
                                                            @NotNull TooltipContent tooltipContent) {
        return new DefaultObjectTypeContainer(renderedComponent, title, tooltipContent);
    }

    public static DisposablePanel pushTop(JComponent component) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, NORTH);
        panel.add(Box.createGlue(), CENTER);
        return panel;
    }

    public static DisposablePanel pushLeft(JComponent component) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, WEST);
        panel.add(Box.createGlue(), CENTER);
        return panel;
    }

    public static DisposablePanel pushRight(JComponent component) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, EAST);
        panel.add(Box.createGlue(), CENTER);
        return panel;
    }

    public static DisposablePanel pushCenter(JComponent component) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, CENTER);
        return panel;
    }

    public static DisposablePanel pushCenter(JComponent component, Border border) {
        DisposablePanel panel = pushCenter(component);
        panel.setBorder(border);
        return panel;
    }

    public static DisposableScrollPane makeItScrollable(JComponent panel) {
        DisposableScrollPane scrollPane = new DisposableScrollPane();
        scrollPane.setViewportView(panel);
        scrollPane.createVerticalScrollBar();
        scrollPane.setBorder(Borders.empty());
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private static DisposablePanel createLabelNextToComponent(JLabel label, JComponent body, boolean outerBorder) {
        Border iconBorder;
        if (outerBorder) {
            Border iconOutside = Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_BORDER, 1, 1, 1, 0);
            Border iconInside = Borders.empty(0, 4);
            iconBorder = new CompoundBorder(iconOutside, iconInside);
        } else {
            Border iconOutside = Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_BORDER, 0, 0, 0, 1);
            Border iconInside = Borders.empty(0, 4);
            iconBorder = new CompoundBorder(iconOutside, iconInside);
        }

        label.setBorder(iconBorder);

        Border bodyBorder;
        if (outerBorder) {
            Border bodyOutside = Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_BORDER, 1, 1, 1, 1);
            Border bodyInside = Borders.empty(0, 2);
            bodyBorder = new CompoundBorder(bodyOutside, bodyInside);
        } else {
            bodyBorder = Borders.empty(0, 2);
        }
        body.setBorder(bodyBorder);

        DisposablePanel wrapper = new DisposablePanel(new BorderLayout());
        wrapper.add(label, WEST);
        wrapper.add(body, CENTER);
        return wrapper;
    }
}
