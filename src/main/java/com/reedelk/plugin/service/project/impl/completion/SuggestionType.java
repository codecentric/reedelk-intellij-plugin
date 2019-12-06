package com.reedelk.plugin.service.project.impl.completion;

import javax.swing.*;

import static com.intellij.icons.AllIcons.Nodes.Method;
import static com.intellij.icons.AllIcons.Nodes.Variable;

public enum SuggestionType {

    FUNCTION(Method),
    VARIABLE(Variable),
    UNKNOWN(null);

    private Icon icon;

    SuggestionType(Icon icon) {
        this.icon = icon;
    }

    public Icon icon() {
        return icon;
    }
}
