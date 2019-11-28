package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.util.ProcessingContext;
import com.reedelk.plugin.completion.token.RootTokens;
import com.reedelk.plugin.completion.token.Token;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class ScriptCompletionContributor extends CompletionContributor {

    public ScriptCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                new DefaultCompletionProvider());
    }

    class DefaultCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            String text = parameters.getPosition().getText();

            // Need to find space, or new line or it is  position 0
            int count = parameters.getOffset();
            if (count == 0 || count == 1) return;

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

            String upToIt = parameters.getPosition().getText().substring(index, parameters.getOffset());

            if (StringUtils.isBlank(upToIt)) {
                // Might be all \n\n\n up to it, hence we can't provide any suggestion.
                return;
            }

            // Start
            Token current = new RootTokens();
            String[] splits = upToIt.split("\\.");
            String[] subSplits = Arrays.copyOfRange(splits, 1, splits.length);
            addResults(current, splits[0], subSplits, result);
        }
    }

    private static void addResults(Token current, String split, String[] splits, CompletionResultSet result) {
        Collection<Token> children = current.children();
        for (Token child : children) {
            if (child.base().equals(split)) {
                if (splits.length == 0) {
                    // We add all the children:
                    child.children().forEach(token -> {
                        // We stop
                        result.addElement(LookupElementBuilder.create(token.base())
                                .withTypeText("TypedContent")
                                .withIcon(AllIcons.Nodes.Method));
                    });

                } else if (splits.length == 1) {
                    // Recursively move on with remaining splits and children
                    addResults(child, splits[0], new String[]{}, result);
                } else {
                    // Recursively move on with remaining splits and children
                    String[] subSplits = Arrays.copyOfRange(splits, 1, splits.length);
                    addResults(child, splits[0], subSplits, result);
                }


            } else if ((child.base()).startsWith(split)){
                // We stop
                result.addElement(LookupElementBuilder.create(child.base())
                        .withTypeText("TypedContent")
                        .withIcon(AllIcons.Nodes.Method));
            }
        }
    }
}
