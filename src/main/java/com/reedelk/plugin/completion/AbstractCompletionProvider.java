package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.ScriptEditor;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.intellij.icons.AllIcons.Nodes.Method;
import static com.intellij.icons.AllIcons.Nodes.Variable;
import static com.reedelk.plugin.userdata.ScriptEditorKey.COMPONENT_FULLY_QUALIFIED_NAME;
import static com.reedelk.plugin.userdata.ScriptEditorKey.MODULE_NAME;

abstract class AbstractCompletionProvider extends CompletionProvider<CompletionParameters> {

    /**
     * We only compute completions if the editor contains module name property and
     * the project associated with it is not null. The module name is set when the ScriptEditor
     * is created by a DynamicValueField or ScriptEditorDefault and so on ...
     *
     * @see ScriptEditor#ScriptEditor(Module module, Document document, ContainerContext context)
     */
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {

        String moduleName = parameters.getEditor().getUserData(MODULE_NAME);
        String componentFullyQualifiedName = parameters.getEditor().getUserData(COMPONENT_FULLY_QUALIFIED_NAME);
        Project project = parameters.getEditor().getProject();

        if (moduleName != null && project != null) {
            computeResultSet(parameters, result, moduleName, componentFullyQualifiedName, project);
        }
    }

    private void computeResultSet(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String moduleName, String componentFullyQualifiedName, Project project) {
        getToken(parameters).ifPresent(token -> {
            Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
            if (module == null) return;

            CompletionService instance = CompletionService.getInstance(module);
            List<Suggestion> suggestions = instance.autocompleteSuggestionOf(componentFullyQualifiedName, token);

            suggestions.forEach(suggestion -> {
                final LookupElementBuilder lookupBuilder =
                        LookupElementBuilder.create(suggestion.getSuggestion())
                                .withPresentableText(suggestion.getSignature())
                                .withTypeText(suggestion.getReturnType())
                                .withIcon(suggestion.getItemType() == AutocompleteItemType.FUNCTION ? Method : Variable);

                // For some suggestions like for .put('') we must adjust the caret back by X positions.
                // If the suggestion definition has defined an offset, then we add an insert handler
                // to move the caret back by X positions accordingly.
                LookupElementBuilder finalLookupBuilder =
                        lookupBuilder.withInsertHandler((insertionContext, item) -> {
                            int currentOffset = insertionContext.getEditor().getCaretModel().getOffset();
                            insertionContext.getEditor().getCaretModel().moveToOffset(currentOffset - suggestion.getCursorOffset());
                        });

                result.addElement(finalLookupBuilder);
            });
        });
    }

    abstract Optional<String> getToken(@NotNull CompletionParameters parameters);
}
