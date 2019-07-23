package com.esb.plugin.editor.properties.widget.input.script.trie;

import com.esb.plugin.editor.properties.widget.input.script.ScriptContextManager;

import java.util.Set;

public class TreeBuilderResult {

    public final Trie tree;
    public final Set<ScriptContextManager.ContextVariable> contextVariables;

    TreeBuilderResult(Trie tree, Set<ScriptContextManager.ContextVariable> contextVariables) {
        this.tree = tree;
        this.contextVariables = contextVariables;
    }
}
