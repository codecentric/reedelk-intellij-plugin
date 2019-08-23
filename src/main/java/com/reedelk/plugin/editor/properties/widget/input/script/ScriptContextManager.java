package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.Arrays.asList;

public class ScriptContextManager implements SuggestionProvider, InputChangeListener {

    private final Module module;
    private final ContainerContext panelContext;
    private final List<VariableDefinition> variableDefinitions;
    private final Set<ContextVariable> contextVariables = new HashSet<>();

    private SuggestionTree suggestionTree;

    public ScriptContextManager(@NotNull Module module,
                                @NotNull ContainerContext panelContext,
                                @NotNull List<VariableDefinition> variableDefinitions) {
        this.module = module;
        this.panelContext = panelContext;
        this.variableDefinitions = variableDefinitions;

        SuggestionTreeBuilderResult builderResult = SuggestionTreeBuilder.get()
                .variables(variableDefinitions)
                .contextPropertyListener(this)
                .context(panelContext)
                .module(module)
                .build();

        this.suggestionTree = builderResult.tree;

        // We add to the context variables panel the default variables
        // and the variables coming from the variable contexts.
        this.contextVariables.addAll(DefaultScriptVariables.ALL);
        this.contextVariables.addAll(builderResult.contextVariables);
    }

    @NotNull
    @Override
    public List<Suggestion> suggest(String prefixText) {
        Set<Suggestion> suggestions = suggestionTree.searchByPrefix(prefixText);
        List<Suggestion> sortedList = new ArrayList<>(suggestions);

        // Order results by the order defined
        // by the suggestion's type.
        sortedList.sort(Comparator.comparing(Suggestion::getSuggestionType));
        return sortedList;
    }

    @Override
    public void onChange(Object value) {
        SuggestionTreeBuilderResult builderResult = SuggestionTreeBuilder.get()
                .variables(variableDefinitions)
                .context(panelContext)
                .module(module)
                .build();

        this.suggestionTree = builderResult.tree;

        // Reload context variables
        this.contextVariables.addAll(DefaultScriptVariables.ALL);
        this.contextVariables.addAll(builderResult.contextVariables);
    }

    public Set<ContextVariable> getVariables() {
        return Collections.unmodifiableSet(contextVariables);
    }

    private static class DefaultScriptVariables {
        static final List<ScriptContextManager.ContextVariable> ALL = asList(
                new ScriptContextManager.ContextVariable("payload", Type.ANY.displayName()),
                new ScriptContextManager.ContextVariable("message", Type.MESSAGE.displayName()),
                new ScriptContextManager.ContextVariable("inboundProperties", Type.MAP.displayName()),
                new ScriptContextManager.ContextVariable("outboundProperties", Type.MAP.displayName()));
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
