package com.esb.plugin.editor.properties.widget.input.script.suggestion;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ListCellRendererWrapper;

import javax.swing.*;

public class SuggestionCellRenderer extends ListCellRendererWrapper<Suggestion> {

    @Override
    public void customize(JList list, Suggestion value, int index, boolean selected, boolean hasFocus) {
        if (SuggestionType.VARIABLE.equals(value.getSuggestionType())) {
            setIcon(AllIcons.Nodes.Field);
        } else if (SuggestionType.PROPERTY.equals(value.getSuggestionType())) {
            setIcon(AllIcons.Nodes.Property);
        } else {
            setIcon(AllIcons.Nodes.EmptyNode);
        }
        setText(value.getToken());
    }
}
