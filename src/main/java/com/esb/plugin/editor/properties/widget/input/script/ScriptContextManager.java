package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.component.domain.AutocompleteContext;
import com.esb.plugin.component.domain.AutocompleteVariable;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;
import com.esb.plugin.jsonschema.JsonSchemaSuggestionTokenizer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class ScriptContextManager implements SuggestionProvider {

    private final List<ContextVariable> DEFAULT_VARIABLES = Arrays.asList(
            new ContextVariable("message", "Message"),
            new ContextVariable("input", "Any"),
            new ContextVariable("inboundProperties", "Map"),
            new ContextVariable("outboundProperties", "Map"));

    private final Trie suggestionTree;
    private final List<ContextVariable> contextVariables = new ArrayList<>();

    public ScriptContextManager(@NotNull Module module,
                                @NotNull PropertyPanelContext panelContext,
                                @NotNull List<AutocompleteVariable> autocompleteVariables) {
        this.suggestionTree = new Trie();

        MessageSuggestions.SUGGESTIONS.forEach(suggestionTree::insert);
        JavascriptKeywords.KEYWORDS.forEach(suggestionTree::insert);

        this.contextVariables.addAll(DEFAULT_VARIABLES);

        autocompleteVariables.forEach(autocompleteVariable -> {
            String variableName = autocompleteVariable.getVariableName();
            String contextName = autocompleteVariable.getContextName();
            // Find related context in this or any other variable

            findAutocompleteContextByName(panelContext, contextName).ifPresent(autocompleteContext -> {
                String propertyName = autocompleteContext.getPropertyName();
                String fileName = panelContext.getPropertyValue(propertyName);
                panelContext.subscribe(propertyName,
                        (InputChangeListener<String>) value -> System.out.println("value"));


                ModuleUtils.getResourcesFolder(module).ifPresent(resourcesFolderPath -> {

                    VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(VirtualFileManager.constructUrl("file", resourcesFolderPath + "/" + fileName));

                    JsonSchemaSuggestionTokenizer parser = new JsonSchemaSuggestionTokenizer(module, file);
                    JsonSchemaSuggestionTokenizer.SchemaDescriptor read = parser.read(variableName);
                    contextVariables.add(new ContextVariable(variableName, read.getType().displayName()));
                    suggestionTree.insert(variableName);
                    read.getTokens().forEach(new Consumer<String>() {
                        @Override
                        public void accept(String suggestionToken) {
                            suggestionTree.insert(suggestionToken);
                        }
                    });
                });
            });
        });
    }

    private Optional<AutocompleteContext> findAutocompleteContextByName(@NotNull PropertyPanelContext panelContext, String contextName) {
        Optional<ComponentPropertyDescriptor> descriptorMatching = panelContext.getDescriptorMatching(descriptor -> descriptor.getAutocompleteContexts()
                .stream()
                .anyMatch(autocompleteContext ->
                        autocompleteContext.getContextName().equals(contextName)));
        return descriptorMatching.flatMap(descriptor -> descriptor.getAutocompleteContexts().stream()
                .filter(autocompleteContext -> autocompleteContext.getContextName().equals(contextName)).findFirst());
    }


    @Override
    public Set<Suggestion> suggest(String text) {
        Set<Suggestion> strings = suggestionTree.searchByPrefix(text);
        if (strings == null) {
            return new HashSet<>();
        }
        return strings;
    }

    public List<ContextVariable> getVariables() {
        return Collections.unmodifiableList(contextVariables);
    }


    public static class ContextVariable {
        public final String name;
        public String type;

        public ContextVariable(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
