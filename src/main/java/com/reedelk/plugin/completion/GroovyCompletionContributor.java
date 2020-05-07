package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.reedelk.plugin.commons.UserData.*;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class GroovyCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        String moduleName = editor.getUserData(MODULE_NAME);
        String componentFullyQualifiedName = editor.getUserData(COMPONENT_PROPERTY_PATH);
        ContainerContext context = editor.getUserData(PROPERTY_CONTEXT);

        if (project != null &&
                context != null &&
                isNotBlank(moduleName) &&
                isNotBlank(componentFullyQualifiedName)) {
            String predecessorFQN = context.predecessor();
            computeResultSet(parameters, result, moduleName, componentFullyQualifiedName, predecessorFQN, project);
        }
    }

    private void computeResultSet(@NotNull CompletionParameters parameters,
                                  @NotNull CompletionResultSet result,
                                  @NotNull String moduleName,
                                  @NotNull String componentFullyQualifiedName,
                                  @Nullable String inputFullyQualifiedName,
                                  @NotNull Project project) {
        Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        if (module == null) return;

        TokenFinder.find(parameters).ifPresent(tokens -> {
            ComponentService instance = ComponentService.getInstance(module);
            Collection<Suggestion> suggestions = instance.suggestionsOf(inputFullyQualifiedName, componentFullyQualifiedName, tokens);
            suggestions.forEach(suggestion -> addSuggestion(result, suggestion));
        });
    }

    private void addSuggestion(@NotNull CompletionResultSet result, Suggestion suggestion) {
        final LookupElementBuilder lookupBuilder =
                LookupElementBuilder.create(suggestion.lookupString())
                        .withPresentableText(suggestion.presentableText())
                        .withTypeText(suggestion.presentableType())
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
