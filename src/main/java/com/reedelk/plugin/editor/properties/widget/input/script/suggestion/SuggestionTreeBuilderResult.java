package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;

import java.util.Set;

public class SuggestionTreeBuilderResult {

    public final SuggestionTree tree;
    public final Set<ScriptContextManager.ContextVariable> contextVariables;

    SuggestionTreeBuilderResult(SuggestionTree tree, Set<ScriptContextManager.ContextVariable> contextVariables) {
        this.tree = tree;
        this.contextVariables = contextVariables;
    }
}
