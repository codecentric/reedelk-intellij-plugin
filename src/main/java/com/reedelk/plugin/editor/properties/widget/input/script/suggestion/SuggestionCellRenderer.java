package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.Nodes;
import static com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionType.*;

public class SuggestionCellRenderer implements ListCellRenderer<Suggestion> {

    private final int suggestionItemWidth = 400;
    private final int suggestionItemTopBottomPadding = 2;
    private final int suggestionItemLeftRightPadding = 5;

    private final ListCellRenderer<Suggestion> myDefaultRenderer;

    @SuppressWarnings("unchecked")
    SuggestionCellRenderer() {
        myDefaultRenderer = new ComboBox().getRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Suggestion> list, Suggestion value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = myDefaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (component instanceof JLabel) {

            JLabel label = (JLabel) component;

            label.setBorder(JBUI.Borders.empty(suggestionItemTopBottomPadding, suggestionItemLeftRightPadding));

            int height = label.getFontMetrics(component.getFont()).getHeight();

            label.setPreferredSize(new Dimension(suggestionItemWidth, height + suggestionItemTopBottomPadding + suggestionItemTopBottomPadding));

            if (VARIABLE.equals(value.getSuggestionType())) {
                label.setIcon(Nodes.Field);
            } else if (PROPERTY.equals(value.getSuggestionType())) {
                label.setIcon(Nodes.Property);
            } else if (FUNCTION.equals(value.getSuggestionType())) {
                label.setIcon(Nodes.Method);
            } else {
                label.setIcon(Nodes.EmptyNode);
            }

            label.setText(value.getToken());
        }

        return component;
    }
}
