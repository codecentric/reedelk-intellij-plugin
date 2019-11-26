package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class ScriptCompletionContributor extends CompletionContributor {

    public ScriptCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

                        String text = parameters.getPosition().getText();

                        // Need to find space, or new line or it is  position 0
                        int count = parameters.getOffset();
                        if (count == 0) return;

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
                        if (upToIt.startsWith("message.")) {
                            result.addElement(LookupElementBuilder.create("content()")
                                    .withTypeText("TypedContent")
                                    .withIcon(AllIcons.Nodes.Method));


                            result.addElement(LookupElementBuilder.create("attributes()")
                                    .withTypeText("MessageAttributes")
                                    .withIcon(AllIcons.Nodes.Method));
                        } else {
                            result.addElement(LookupElementBuilder.create("message")
                                    .withTypeText("Message")
                                    .withIcon(AllIcons.Nodes.Field));


                            result.addElement(LookupElementBuilder.create("context")
                                    .withTypeText("Context")
                                    .withIcon(AllIcons.Nodes.Field));
                        }

                    }
                });
    }
}
