package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.component.domain.VariableDefinition;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.script.trie.SuggestionTreeBuilder;
import com.esb.plugin.editor.properties.widget.input.script.trie.TreeBuilderResult;
import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ScriptContextManager implements SuggestionProvider, InputChangeListener {

    private final Module module;
    private final PropertyPanelContext panelContext;
    private final List<VariableDefinition> variableDefinitions;
    private final Set<ContextVariable> contextVariables = new HashSet<>();

    private Trie suggestionTree;

    public ScriptContextManager(@NotNull Module module,
                                @NotNull PropertyPanelContext panelContext,
                                @NotNull List<VariableDefinition> variableDefinitions) {
        this.module = module;
        this.panelContext = panelContext;
        this.variableDefinitions = variableDefinitions;

        TreeBuilderResult treeBuilderResult = SuggestionTreeBuilder.get()
                .variables(variableDefinitions)
                .contextPropertyListener(this)
                .context(panelContext)
                .module(module)
                .build();

        this.suggestionTree = treeBuilderResult.tree;

        // We add to the context variables panel the default variables
        // and the variables coming from the variable contexts.
        this.contextVariables.addAll(DefaultScriptVariables.ALL);
        this.contextVariables.addAll(treeBuilderResult.contextVariables);
    }

    @NotNull
    @Override
    public List<Suggestion> suggest(String text) {
        Set<Suggestion> suggestions = suggestionTree.searchByPrefix(text);
        List<Suggestion> sortedList = new ArrayList<>(suggestions);

        // Order results by the order defined by the type of suggestion
        sortedList.sort(Comparator.comparing(Suggestion::getSuggestionType));
        return sortedList;
    }

    @Override
    public void onChange(Object value) {
        TreeBuilderResult treeBuilderResult = SuggestionTreeBuilder.get()
                .variables(variableDefinitions)
                .context(panelContext)
                .module(module)
                .build();

        this.suggestionTree = treeBuilderResult.tree;

        // Reload context variables
        this.contextVariables.addAll(DefaultScriptVariables.ALL);
        this.contextVariables.addAll(treeBuilderResult.contextVariables);
    }

    Set<ContextVariable> getVariables() {
        return Collections.unmodifiableSet(contextVariables);
    }

    public static class ContextVariable {
        public final String name;
        public String type;

        public ContextVariable(String name, String type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ContextVariable that = (ContextVariable) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
