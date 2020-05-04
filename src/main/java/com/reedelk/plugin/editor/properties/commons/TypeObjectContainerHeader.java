package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.TooltipContent;
import com.reedelk.plugin.editor.properties.ClickableTooltip;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Optional;
import java.util.function.Function;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.IconAlignment.LEFT;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.OnClickAction;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class TypeObjectContainerHeader extends DisposablePanel {

    public TypeObjectContainerHeader(String displayName, Icon icon, OnClickAction labelClickListener) {
        this(new ClickableLabel(displayName, icon, LEFT, labelClickListener), null);
    }

    public TypeObjectContainerHeader(String displayName, TooltipContent tooltipContent, Icon icon, OnClickAction labelClickListener) {
        this(new ClickableLabel(displayName, icon, LEFT, labelClickListener), tooltipContent);
    }

    public TypeObjectContainerHeader(String displayName, TooltipContent tooltipContent) {
        this(new JBLabel(displayName), tooltipContent);
    }

    private TypeObjectContainerHeader(JLabel label, @Nullable TooltipContent tooltipContent) {
        label.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
        label.setOpaque(false);

        HorizontalSeparator separator = new HorizontalSeparator();
        separator.setOpaque(false);

        JComponent westComponent = Optional.ofNullable(tooltipContent)
                .map(content -> content.build()
                        .map((Function<String, JComponent>) tooltipText -> {
                            ClickableTooltip toolTip = new ClickableTooltip(tooltipText);
                            DisposablePanel panel = ContainerFactory.createLabelNextToComponent(label, toolTip);
                            panel.setOpaque(false);
                            return panel;
                        }).orElse(label)).orElse(label);

        setOpaque(false);
        setLayout(new BorderLayout());
        add(westComponent, WEST);
        add(separator, CENTER);
    }

    private static class HorizontalSeparator extends DisposablePanel {
        public HorizontalSeparator() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JSeparator jSeparator = new JSeparator();
            jSeparator.setForeground(Colors.TYPE_OBJECT_HORIZONTAL_SEPARATOR);
            add(jSeparator, gbc);

            setBorder(BORDER_HORIZONTAL_SEPARATOR_CONTAINER);
        }
    }

    static final Border BORDER_HORIZONTAL_SEPARATOR_CONTAINER = empty(2, 5, 0, 0);
}
