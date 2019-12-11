package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

class CompletionProviderCommunityEdition extends AbstractCompletionProvider {

    @Override
    Optional<String> getToken(@NotNull CompletionParameters parameters) {
        String text = parameters.getPosition().getText();
        int offset = parameters.getOffset();
        return findLastToken(text, offset);
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
