package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PlainTextTokenTypes;
import com.reedelk.plugin.commons.DefaultConstants;

import static com.intellij.patterns.PlatformPatterns.psiElement;

// TODO: Check be after switch to Groovy Language.
public class ScriptCompletionContributor extends CompletionContributor {

    public ScriptCompletionContributor() {
        if (DefaultConstants.SCRIPT_LANGUAGE != null) {
            // The Ultimate edition has support for script Language.
            extend(CompletionType.BASIC,
                    psiElement().withLanguage(DefaultConstants.SCRIPT_LANGUAGE),
                    new CompletionProviderUltimateEdition());
        } else {
            // The Community edition does not have support for script Language.
            extend(CompletionType.BASIC,
                    psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                    new CompletionProviderCommunityEdition());
        }
    }
}
