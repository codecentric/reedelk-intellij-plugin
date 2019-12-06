package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.util.ProcessingContext;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ScriptCompletionContributor extends CompletionContributor {

    public ScriptCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                new DefaultCompletionProvider());
    }

    class DefaultCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            String moduleName = parameters.getEditor().getUserData(ScriptEditor.MODULE_NAME);
            Project project = parameters.getEditor().getProject();
            if (moduleName != null && project != null) {
                Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
                if (module != null) {
                    addCompletions(module, parameters, result);
                }
            }
        }

        private void addCompletions(Module module, CompletionParameters parameters, CompletionResultSet result) {
            String text = parameters.getPosition().getText();
            int offset = parameters.getOffset();
            findLastToken(text, offset).ifPresent(lastToken -> {
                CompletionService instance = CompletionService.getInstance(module);
                instance.completionTokensOf(lastToken).ifPresent(strings -> strings.forEach(suggestion -> {
                    result.addElement(LookupElementBuilder.create(suggestion.getToken())
                            .withTypeText(suggestion.getTypeName())
                            .withIcon(suggestion.getType().icon()));
                }));
            });
        }

        private Optional<String> findLastToken(String text, int offset) {
            // Need to find space, or new line or it is position 0
            int count = offset;
            if (count == 0 || count == 1) return Optional.empty();

            int index;
            while (true) {
                if (count <= 0) {
                    index = count;
                    break;
                }
                count--;
                char c = text.charAt(count);
                if (c == '\n' || c == ' ') {
                    index = count + 1;
                    break;
                }
            }

            // Represent the word starting from a space ' ' or a new line \n or the beginning of the document.
            String token = text.substring(index, offset);
            // IF it is blank, // Might be all \n\n\n up to it, hence we can't provide any suggestion.
            return StringUtils.isBlank(token) ? Optional.empty() : Optional.of(token);
        }
    }
}
