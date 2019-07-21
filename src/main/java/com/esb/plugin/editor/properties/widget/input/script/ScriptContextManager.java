package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.component.domain.AutocompleteVariable;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.script.trie.SuggestionTreeBuilder;
import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ScriptContextManager implements SuggestionProvider, InputChangeListener {

    private final Module module;
    private final PropertyPanelContext panelContext;
    private final List<AutocompleteVariable> autocompleteVariables;
    private final Set<ContextVariable> contextVariables = new HashSet<>();

    private Trie suggestionTree;

    public ScriptContextManager(@NotNull Module module,
                                @NotNull PropertyPanelContext panelContext,
                                @NotNull List<AutocompleteVariable> autocompleteVariables) {
        this.module = module;
        this.panelContext = panelContext;
        this.autocompleteVariables = autocompleteVariables;

        SuggestionTreeBuilder.TreeBuilderResult build = SuggestionTreeBuilder.get()
                .variables(autocompleteVariables)
                .contextPropertyListener(this)
                .context(panelContext)
                .module(module)
                .build();

        this.suggestionTree = build.tree;

        // We add to the context variables panel the default variables
        // and the variables coming from the variable contexts.
        this.contextVariables.addAll(DefaultScriptVariables.ALL);
        this.contextVariables.addAll(build.contextVariables);
    }

    @NotNull
    @Override
    public List<Suggestion> suggest(String text) {
        // Order suggestions
        Set<Suggestion> suggestions = suggestionTree.searchByPrefix(text);
        List<Suggestion> sortedList = new ArrayList<>(suggestions);
        Collections.sort(sortedList,
                Comparator.comparing(Suggestion::getSuggestionType));
        return sortedList;
    }

    @Override
    public void onChange(Object value) {
        SuggestionTreeBuilder.TreeBuilderResult build = SuggestionTreeBuilder.get()
                .variables(autocompleteVariables)
                .context(panelContext)
                .module(module)
                .build();

        this.suggestionTree = build.tree;

        // Reload context variables
        this.contextVariables.addAll(DefaultScriptVariables.ALL);
        this.contextVariables.addAll(build.contextVariables);
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
