package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.icons.AllIcons.Nodes.Method;
import static com.intellij.icons.AllIcons.Nodes.Variable;
import static com.reedelk.plugin.userdata.ScriptEditorKey.COMPONENT_FULLY_QUALIFIED_NAME;
import static com.reedelk.plugin.userdata.ScriptEditorKey.MODULE_NAME;

public class GroovyCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        String moduleName = parameters.getEditor().getUserData(MODULE_NAME);
        String componentFullyQualifiedName = parameters.getEditor().getUserData(COMPONENT_FULLY_QUALIFIED_NAME);
        Project project = parameters.getEditor().getProject();
        if (moduleName != null && project != null) {
            computeResultSet(parameters, result, moduleName, componentFullyQualifiedName, project);
        }
    }

    private void computeResultSet(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, String moduleName, String componentFullyQualifiedName, Project project) {
        if (!TokenFinder.find(parameters).isPresent()) {
            return;
        }
        String token = TokenFinder.find(parameters).get();
        Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        if (module == null) return;

        CompletionService instance = CompletionService.getInstance(module);
        List<Suggestion> suggestions = instance.autocompleteSuggestionOf(componentFullyQualifiedName, token);

        addSuggestions(result, suggestions);
    }

    private void addSuggestions(@NotNull CompletionResultSet result, List<Suggestion> suggestions) {
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
    }
}
