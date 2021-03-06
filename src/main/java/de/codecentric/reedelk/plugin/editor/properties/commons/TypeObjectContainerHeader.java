package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.TooltipContent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Optional;
import java.util.function.Function;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class TypeObjectContainerHeader extends DisposablePanel {

    public TypeObjectContainerHeader(String displayName, Icon icon, ClickableLabel.OnClickAction labelClickListener) {
        this(new ClickableLabel(displayName, icon, ClickableLabel.IconAlignment.LEFT, labelClickListener), null, true);
    }

    public TypeObjectContainerHeader(String displayName, TooltipContent tooltipContent, Icon icon, ClickableLabel.OnClickAction labelClickListener) {
        this(new ClickableLabel(displayName, icon, ClickableLabel.IconAlignment.LEFT, labelClickListener), tooltipContent, true);
    }

    public TypeObjectContainerHeader(String displayName, TooltipContent tooltipContent) {
        this(new JBLabel(displayName), tooltipContent, true);
    }

    public TypeObjectContainerHeader(String displayName, TooltipContent tooltipContent, Icon icon, ClickableLabel.OnClickAction clickAction, boolean horizontalBar, ClickableLabel.IconAlignment iconAlignment) {
        this(new ClickableLabel(displayName, icon, iconAlignment, clickAction), tooltipContent, horizontalBar);
    }

    public TypeObjectContainerHeader(String displayName, TooltipContent tooltipContent, Icon icon, ClickableLabel.OnClickAction clickAction, boolean horizontalBar) {
        this(new ClickableLabel(displayName, icon, ClickableLabel.IconAlignment.LEFT, clickAction), tooltipContent, horizontalBar);
    }

    private TypeObjectContainerHeader(JLabel label, @Nullable TooltipContent tooltipContent, boolean horizontalBar) {
        label.setForeground(Colors.PROPERTIES_PROPERTY_TITLE);
        label.setOpaque(false);

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

        if (horizontalBar) {
            HorizontalSeparator separator = new HorizontalSeparator();
            separator.setOpaque(false);
            add(separator, CENTER);
        }
    }

    public static class HorizontalSeparator extends DisposablePanel {
        public HorizontalSeparator() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JSeparator jSeparator = new JSeparator();
            jSeparator.setForeground(Colors.PROPERTIES_TYPE_OBJECT_HORIZONTAL_SEPARATOR);

            add(jSeparator, gbc);
            setBorder(BORDER_HORIZONTAL_SEPARATOR_CONTAINER);
        }
    }

    static final Border BORDER_HORIZONTAL_SEPARATOR_CONTAINER = empty(2, 5, 0, 0);
}
