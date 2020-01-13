package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public abstract class SimpleListItemRenderer<T> extends JBLabel implements ListCellRenderer<T> {

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        setBorder(JBUI.Borders.empty(1));

        Color bg = isSelected ? list.getSelectionBackground() : list.getBackground();
        Color fg = isSelected ? list.getSelectionForeground() : list.getForeground();
        setBackground(bg);
        setForeground(fg);
        setFont(list.getFont());
        setText(StringUtils.EMPTY);
        setIcon(null);

        customize(list, value, index, isSelected, cellHasFocus);

        setOpaque(isSelected);
        return this;
    }

    @Override
    public Dimension getPreferredSize() {
        if (StringUtil.isNotEmpty(getText())) {
            return super.getPreferredSize();
        }
        setText(" ");
        Dimension size = super.getPreferredSize();
        setText(StringUtils.EMPTY);
        return size;
    }

    public abstract void customize(@NotNull JList<? extends T> list, T value, int index, boolean selected, boolean hasFocus);

}
