package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.util.ProcessingContext;
import com.reedelk.plugin.service.application.CompletionService;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
            findLastToken(parameters.getPosition().getText(), parameters.getOffset()).ifPresent(lastToken -> {
                CompletionService instance = CompletionService.getInstance();
                List<LookupElement> lookupElements = instance.completionTokensOf(lastToken);
                lookupElements.forEach(result::addElement);
            });
        }
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
