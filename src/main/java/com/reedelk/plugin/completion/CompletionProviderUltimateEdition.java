package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @see ScriptCompletionContributor
 */
class CompletionProviderUltimateEdition extends AbstractCompletionProvider {

    Optional<String> getToken(@NotNull CompletionParameters parameters) {
        // Should add completions
        PsiElement parent = parameters.getPosition().getParent();
        if (parent != null) {
            String text = parent.getText();
            int offset = parameters.getOffset() - parent.getTextOffset();
            if (offset < text.length() && offset > 1) {
                return Optional.of(text.substring(0, offset));
            }
        }
        return Optional.empty();
    }
}
