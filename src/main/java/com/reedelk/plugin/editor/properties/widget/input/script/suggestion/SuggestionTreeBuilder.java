package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.commons.ParentFolder;
import com.reedelk.plugin.commons.ProjectFileContentProvider;
import com.reedelk.plugin.component.domain.AutocompleteContext;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.PropertyPanelContext;
import com.reedelk.plugin.editor.properties.widget.input.script.Type;
import com.reedelk.plugin.jsonschema.JsonSchemaProjectClient;
import com.reedelk.plugin.jsonschema.JsonSchemaSuggestionsProcessor;
import com.reedelk.plugin.jsonschema.JsonSchemaSuggestionsResult;
import com.reedelk.runtime.api.annotation.AutocompleteType;
import org.everit.json.schema.loader.SchemaClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager.ContextVariable;
import static com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionType.PROPERTY;
import static com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionType.VARIABLE;
import static com.reedelk.runtime.commons.Preconditions.checkState;
import static com.reedelk.runtime.commons.StringUtils.isNotBlank;

public class SuggestionTreeBuilder {

    private Module module;
    private SuggestionTree suggestionTree;
    private InputChangeListener<?> listener;
    private PropertyPanelContext panelContext;
    private List<VariableDefinition> variableDefinitions;

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

    public SuggestionTreeBuilder variables(List<VariableDefinition> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
        return this;
    }

    public SuggestionTreeBuilder contextPropertyListener(InputChangeListener<?> listener) {
        this.listener = listener;
        return this;
    }

    public SuggestionTreeBuilderResult build() {
        checkState(module != null, "module");
        checkState(panelContext != null, "panelContext");
        checkState(variableDefinitions != null, "variableDefinitions");

        suggestionTree = new SuggestionTree();
        PredefinedMessageSuggestions.SUGGESTIONS.forEach(suggestionTree::insert);
        PredefinedJavascriptSuggestions.KEYWORDS.forEach(suggestionTree::insert);

        Set<ContextVariable> contextVariables = new HashSet<>();

        variableDefinitions.forEach(variableDefinition -> {

            String variableName = variableDefinition.getVariableName();
            String contextName = variableDefinition.getContextName();

            // The variable needs to be added to the context variables panel
            ContextVariable contextVariable = new ContextVariable(variableName, Type.OBJECT.displayName());
            contextVariables.add(contextVariable);

            // The variable needs to be added to the tree to enable suggestion
            suggestionTree.insert(new SuggestionToken(variableName, VARIABLE));

            buildVariableContext(variableName, contextName);
        });

        return new SuggestionTreeBuilderResult(suggestionTree, contextVariables);
    }

    private void buildVariableContext(String variableName, String contextName) {
        // Find the autocomplete context related to this variable
        // in this or any other variable defined in the descriptors.
        findAutocompleteContextByName(panelContext, contextName).ifPresent(autocompleteContext -> {
            String propertyName = autocompleteContext.getPropertyName();
            AutocompleteType autocompleteType = autocompleteContext.getAutocompleteType();

            if (AutocompleteType.JSON_SCHEMA.equals(autocompleteType)) {
                String jsonSchemaRelativeFileName = panelContext.getPropertyValue(propertyName);
                if (isNotBlank(jsonSchemaRelativeFileName)) {
                    populateSuggestionTokensFromJsonSchema(module, variableName, jsonSchemaRelativeFileName);
                }
            }

            if (listener != null) {
                panelContext.subscribe(propertyName, listener);
            }
        });
    }

    private void populateSuggestionTokensFromJsonSchema(Module module, String variableName, String fileName) {
        ModuleUtils.getResourcesFolder(module).ifPresent(resourcesFolderPath -> {

            // The file name URL of the json schema is relative to the resources folder
            String jsonSchemaUrl =
                    VirtualFileManager.constructUrl("file", resourcesFolderPath + "/" + fileName);


            ProjectFileContentProvider.of(jsonSchemaUrl).ifPresent(json -> {

                String parentFolder = ParentFolder.of(jsonSchemaUrl);

                JSONObject schemaJsonObject = new JSONObject(new JSONTokener(json));

                String rootPath = getRootPath(schemaJsonObject);

                SchemaClient schemaClient = new JsonSchemaProjectClient(module, parentFolder, rootPath);

                JsonSchemaSuggestionsProcessor processor = new JsonSchemaSuggestionsProcessor(schemaJsonObject, schemaClient);
                JsonSchemaSuggestionsResult suggestionResult = processor.process();

                // All the properties of this variable extracted from the json schema
                // need to be added to the suggestion tree.
                suggestionResult
                        .getTokens()
                        .stream()
                        .map(token -> variableName + "." + token) // append the parent variable name to each token
                        .map(token -> new SuggestionToken(token, PROPERTY))
                        .forEach(suggestionTree::insert);
            });
        });
    }

    /**
     * It finds the AutocompleteContext definition in any property descriptor
     * matching the given context name.
     */
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
