package com.esb.plugin.editor.properties.widget.input.script.trie;

import com.esb.api.annotation.AutocompleteType;
import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.component.domain.AutocompleteContext;
import com.esb.plugin.component.domain.AutocompleteVariable;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.script.JavascriptKeywords;
import com.esb.plugin.editor.properties.widget.input.script.MessageSuggestions;
import com.esb.plugin.editor.properties.widget.input.script.ProjectFileContentProvider;
import com.esb.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.esb.plugin.jsonschema.JsonSchemaSuggestionTokenizer;
import com.esb.plugin.jsonschema.JsonSchemaSuggestionTokenizer.SchemaDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.esb.plugin.editor.properties.widget.input.script.ScriptContextManager.ContextVariable;

public class SuggestionTreeBuilder {

    private Module module;
    private Trie suggestionTree;
    private InputChangeListener<?> listener;
    private PropertyPanelContext panelContext;
    private List<AutocompleteVariable> autocompleteVariables;
    private final Set<ContextVariable> contextVariables = new HashSet<>();

    public static SuggestionTreeBuilder get() {
        return new SuggestionTreeBuilder();
    }

    public SuggestionTreeBuilder module(Module module) {
        this.module = module;
        return this;
    }

    public SuggestionTreeBuilder context(PropertyPanelContext context) {
        this.panelContext = context;
        return this;
    }

    public SuggestionTreeBuilder variables(List<AutocompleteVariable> autocompleteVariables) {
        this.autocompleteVariables = autocompleteVariables;
        return this;
    }

    public SuggestionTreeBuilder contextPropertyListener(InputChangeListener<?> listener) {
        this.listener = listener;
        return this;
    }

    public TreeBuilderResult build() {
        this.suggestionTree = new Trie();

        MessageSuggestions.SUGGESTIONS.forEach(suggestionTree::insert);
        JavascriptKeywords.KEYWORDS.forEach(suggestionTree::insert);

        autocompleteVariables.forEach(this::processAutocompleteVariable);

        return new TreeBuilderResult(suggestionTree, contextVariables);
    }

    private void processAutocompleteVariable(AutocompleteVariable autocompleteVariable) {
        String variableName = autocompleteVariable.getVariableName();
        String contextName = autocompleteVariable.getContextName();
        // Find the autocomplete context related to this variable
        // in this or any other variable defined in the descriptors.
        findAutocompleteContextByName(panelContext, contextName).ifPresent(autocompleteContext -> {
            String propertyName = autocompleteContext.getPropertyName();
            AutocompleteType autocompleteType = autocompleteContext.getAutocompleteType();

            if (listener != null) {
                panelContext.subscribe(propertyName, listener);
            }

            if (autocompleteType.equals(AutocompleteType.JSON_SCHEMA)) {
                String fileName = panelContext.getPropertyValue(propertyName);
                extractSuggestionTokensFromJsonSchema(module, variableName, fileName);
            }
        });
    }

    private void extractSuggestionTokensFromJsonSchema(@NotNull Module module,
                                                       @NotNull String variableName,
                                                       @NotNull String fileName) {

        ModuleUtils.getResourcesFolder(module).ifPresent(resourcesFolderPath -> {

            String jsonSchemaFileUrl = VirtualFileManager.constructUrl("file", resourcesFolderPath + "/" + fileName);

            ProjectFileContentProvider provider = new ProjectFileContentProvider();
            String json = provider.getContent(jsonSchemaFileUrl);
            String parentFolder = provider.getParentFolder(jsonSchemaFileUrl);

            JsonSchemaSuggestionTokenizer parser = new JsonSchemaSuggestionTokenizer(module, json, parentFolder, provider);
            SchemaDescriptor tokenizedSchema = parser.read(variableName);

            ScriptContextManager.ContextVariable contextVariable = new ScriptContextManager.ContextVariable(variableName, tokenizedSchema.getType().displayName());
            contextVariables.add(contextVariable);

            suggestionTree.insert(variableName);
            tokenizedSchema.getTokens().forEach(suggestionTree::insert);
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

    public class TreeBuilderResult {
        public final Trie tree;
        public final Set<ContextVariable> contextVariables;

        private TreeBuilderResult(Trie tree, Set<ContextVariable> contextVariables) {
            this.tree = tree;
            this.contextVariables = contextVariables;
        }
    }
}
