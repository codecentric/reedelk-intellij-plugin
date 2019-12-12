package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.userdata.ScriptEditorKey.COMPONENT_FULLY_QUALIFIED_NAME;
import static com.reedelk.plugin.userdata.ScriptEditorKey.MODULE_NAME;

abstract class AbstractCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {

        String moduleName = parameters.getEditor().getUserData(MODULE_NAME);
        String componentFullyQualifiedName = parameters.getEditor().getUserData(COMPONENT_FULLY_QUALIFIED_NAME);
        Project project = parameters.getEditor().getProject();

        if (moduleName != null && project != null) {
            getToken(parameters).ifPresent(token -> {
                Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
                if (module == null) return;

                CompletionService instance = CompletionService.getInstance(module);
                List<Suggestion> suggestions = instance.completionTokensOf(componentFullyQualifiedName, token);
                suggestions.forEach(suggestion -> {

                    final LookupElementBuilder lookupBuilder = LookupElementBuilder.create(suggestion.getToken())
                            .withTypeText(suggestion.getTypeName())
                            .withIcon(suggestion.getType().icon());

                    // For some suggestions like for .put('') we must adjust the caret back by X positions.
                    // If the suggestion definition has defined an offset, then we add an insert handler
                    // to move the caret back by X positions accordingly.
                    LookupElementBuilder finalLookupBuilder = suggestion.getOffset().map(offset ->
                            lookupBuilder.withInsertHandler((insertionContext, item) -> {
                                int currentOffset = insertionContext.getEditor().getCaretModel().getOffset();
                                insertionContext.getEditor().getCaretModel().moveToOffset(currentOffset - offset);
                            })).orElse(lookupBuilder);

                    result.addElement(finalLookupBuilder);
                });
            });
        }
    }

    abstract Optional<String> getToken(@NotNull CompletionParameters parameters);
}