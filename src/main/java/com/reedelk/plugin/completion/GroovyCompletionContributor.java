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
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        if (module == null) return;

        TokenFinder.find(parameters).ifPresent(tokens -> {
            CompletionService instance = CompletionService.getInstance(module);
            List<Suggestion> suggestions = instance.autocompleteSuggestionOf(componentFullyQualifiedName, tokens);
            suggestions.forEach(suggestion -> addSuggestion(result, suggestion));
        });
    }

    private void addSuggestion(@NotNull CompletionResultSet result, Suggestion suggestion) {
        final LookupElementBuilder lookupBuilder =
                LookupElementBuilder.create(suggestion.lookupString())
                        .withPresentableText(suggestion.presentableText())
                        .withTypeText(suggestion.typeText())
                        .withIcon(suggestion.icon());

        // For some suggestions like for .put('') we must adjust the caret back by X positions.
        // If the suggestion definition has defined an offset, then we add an insert handler
        // to move the caret back by X positions accordingly.
        LookupElementBuilder finalLookupBuilder =
                lookupBuilder.withInsertHandler((insertionContext, item) -> {
                    int currentOffset = insertionContext.getEditor().getCaretModel().getOffset();
                    insertionContext.getEditor().getCaretModel().moveToOffset(currentOffset - suggestion.cursorOffset());
                });

        result.addElement(finalLookupBuilder);
    }
}
