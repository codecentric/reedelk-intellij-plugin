package com.esb.plugin.editor.properties.widget.input.script.trie;

import com.esb.api.annotation.AutocompleteType;
import com.esb.internal.commons.StringUtils;
import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.component.domain.AutocompleteContext;
import com.esb.plugin.component.domain.AutocompleteVariable;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.script.JavascriptKeywords;
import com.esb.plugin.editor.properties.widget.input.script.MessageSuggestions;
import com.esb.plugin.editor.properties.widget.input.script.ProjectFileContentProvider;
import com.esb.plugin.editor.properties.widget.input.script.SuggestionToken;
import com.esb.plugin.javascript.Type;
import com.esb.plugin.jsonschema.JsonSchemaProjectClient;
import com.esb.plugin.jsonschema.JsonSchemaSuggestionsProcessor;
import com.esb.plugin.jsonschema.JsonSchemaSuggestionsResult;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.everit.json.schema.loader.SchemaClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.esb.plugin.editor.properties.widget.input.script.ScriptContextManager.ContextVariable;
import static com.esb.plugin.editor.properties.widget.input.script.SuggestionType.PROPERTY;
import static com.esb.plugin.editor.properties.widget.input.script.SuggestionType.VARIABLE;

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

    public SuggestionTreeBuilder module(@NotNull Module module) {
        this.module = module;
        return this;
    }

    public SuggestionTreeBuilder context(@NotNull PropertyPanelContext context) {
        this.panelContext = context;
        return this;
    }

    public SuggestionTreeBuilder variables(@NotNull List<AutocompleteVariable> autocompleteVariables) {
        this.autocompleteVariables = autocompleteVariables;
        return this;
    }

    public SuggestionTreeBuilder contextPropertyListener(@NotNull InputChangeListener<?> listener) {
        this.listener = listener;
        return this;
    }

    public TreeBuilderResult build() {
        this.suggestionTree = new Trie();

        MessageSuggestions.SUGGESTIONS.forEach(suggestionTree::insert);
        JavascriptKeywords.KEYWORDS.forEach(suggestionTree::insert);

        autocompleteVariables.forEach(this::buildAutocompleteVariable);

        return new TreeBuilderResult(suggestionTree, contextVariables);
    }

    private void buildAutocompleteVariable(AutocompleteVariable autocompleteVariable) {
        String variableName = autocompleteVariable.getVariableName();
        String contextName = autocompleteVariable.getContextName();
        // Find the autocomplete context related to this variable
        // in this or any other variable defined in the descriptors.
        Optional<AutocompleteContext> autocompleteContextByName = findAutocompleteContextByName(panelContext, contextName);
        if (autocompleteContextByName.isPresent()) {
            AutocompleteContext autocompleteContext = autocompleteContextByName.get();

            String propertyName = autocompleteContext.getPropertyName();
            AutocompleteType autocompleteType = autocompleteContext.getAutocompleteType();

            if (listener != null) {
                panelContext.subscribe(propertyName, listener);
            }

            if (autocompleteType.equals(AutocompleteType.JSON_SCHEMA)) {
                String fileName = panelContext.getPropertyValue(propertyName);
                if (StringUtils.isNotBlank(fileName)) {
                    suggestionTokensFromJsonSchema(module, variableName, fileName);
                }
            }

        } else {
            ContextVariable contextVariable = new ContextVariable(variableName, Type.OBJECT.displayName());
            contextVariables.add(contextVariable);
        }
    }

    private void suggestionTokensFromJsonSchema(@NotNull Module module,
                                                @NotNull String variableName,
                                                @NotNull String fileName) {

        ModuleUtils.getResourcesFolder(module).ifPresent(resourcesFolderPath -> {

            String jsonSchemaFileUrl = VirtualFileManager.constructUrl("file", resourcesFolderPath + "/" + fileName);

            ProjectFileContentProvider provider = new ProjectFileContentProvider();
            String json = provider.getContent(jsonSchemaFileUrl);
            String parentFolder = provider.getParentFolder(jsonSchemaFileUrl);

            JSONObject schemaJsonObject = new JSONObject(new JSONTokener(json));

            String rootPath = getRootPath(schemaJsonObject);

            SchemaClient schemaClient = new JsonSchemaProjectClient(module, parentFolder, rootPath, provider);

            JsonSchemaSuggestionsProcessor parser = new JsonSchemaSuggestionsProcessor(schemaJsonObject, schemaClient);

            JsonSchemaSuggestionsResult suggestionResult = parser.read();

            // All the properties of this variable extracted from the json schema
            // need to be added to the suggestion tree.
            suggestionResult
                    .getTokens()
                    .stream()
                    .map(token -> variableName + "." + token) // append the parent variable name to each token
                    .map(token -> new SuggestionToken(token, PROPERTY))
                    .forEach(suggestionTree::insert);

            // The variable needs to be added to the context variables panel
            ContextVariable contextVariable = new ContextVariable(variableName, Type.OBJECT.displayName());
            contextVariables.add(contextVariable);

            // The variable needs to be added to the tree to enable suggestion
            suggestionTree.insert(new SuggestionToken(variableName, VARIABLE));
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


    private String getRootPath(JSONObject schemaJsonObject) {
        String rootId = schemaJsonObject.getString("$id");
        int lastSlash = rootId.lastIndexOf("/");
        return rootId.substring(0, lastSlash);
    }
}
