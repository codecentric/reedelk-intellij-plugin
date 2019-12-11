package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PlainTextTokenTypes;
import com.reedelk.plugin.commons.Defaults;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ScriptCompletionContributor extends CompletionContributor {

    public ScriptCompletionContributor() {
        if (Defaults.JAVASCRIPT_LANGUAGE != null) {
            // The Ultimate edition has support for Javascript Language.
            extend(CompletionType.BASIC,
                    psiElement().withLanguage(Defaults.JAVASCRIPT_LANGUAGE),
                    new CompletionProviderUltimateEdition());
        } else {
            // The Community edition does not have support for Javascript Language.
            extend(CompletionType.BASIC,
                    psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                    new CompletionProviderCommunityEdition());
        }
    }
}
