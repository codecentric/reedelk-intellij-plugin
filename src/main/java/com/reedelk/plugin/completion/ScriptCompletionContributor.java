package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.reedelk.plugin.commons.UserData.*;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class ScriptCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        String moduleName = editor.getUserData(MODULE_NAME);
        ComponentContext context = editor.getUserData(COMPONENT_CONTEXT);
        String componentPropertyPath = editor.getUserData(COMPONENT_PROPERTY_PATH);

        if (project != null && context != null && isNotBlank(moduleName) && isNotBlank(componentPropertyPath)) {

            Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
            if (module == null) return;

            String text = parameters.getPosition().getText();
            int offset = parameters.getOffset();
            String[] tokens = Tokenizer.tokenize(text, offset);

            PlatformModuleService instance = PlatformModuleService.getInstance(module);


            // TODO: Fixme, this is to allow suggestions of types inside a closure block.
            int closureStart = -1;
            for (int i = tokens.length - 1; i >= 0; i--) {
                if (tokens[i].equals("{")) {
                    closureStart = i;
                    break;
                }
            }

            if (closureStart >= 0) {
                String[] closure = new String[tokens.length - closureStart - 1];
                System.arraycopy(tokens, closureStart + 1, closure, 0, tokens.length - closureStart - 1);
                Collection<Suggestion> suggestions = instance.suggestionsOf(context, componentPropertyPath, closure);
                suggestions.forEach(suggestion -> addSuggestion(result, suggestion));
            }
            Collection<Suggestion> suggestions = instance.suggestionsOf(context, componentPropertyPath, tokens);
            suggestions.forEach(suggestion -> addSuggestion(result, suggestion));
        }
    }

    private void addSuggestion(@NotNull CompletionResultSet result, Suggestion suggestion) {
        final LookupElementBuilder lookupBuilder =
                LookupElementBuilder.create(suggestion, suggestion.getInsertValue())
                        .withPresentableText(suggestion.getLookupToken())
                        .withTypeText(suggestion.getReturnTypeDisplayValue())
                        .withTailText(suggestion.getTailText())
                        .withIcon(suggestion.getType().icon);

        // For some suggestions like for .put('') we must adjust the caret back by X positions.
        // If the suggestion definition has defined an offset, then we add an insert handler
        // to move the caret back by X positions accordingly.
        LookupElementBuilder finalLookupBuilder =
                lookupBuilder.withInsertHandler((insertionContext, item) -> {
                    int currentOffset = insertionContext.getEditor().getCaretModel().getOffset();
                    insertionContext.getEditor().getCaretModel().moveToOffset(currentOffset - suggestion.getCursorOffset());
                });

        result.addElement(finalLookupBuilder);
    }
}
