package com.reedelk.plugin.service.application.impl;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.reedelk.plugin.service.application.CompletionService;
import com.reedelk.plugin.service.application.impl.completion.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompletionServiceImpl implements CompletionService {

    @Override
    public List<LookupElement> completionTokensOf(String token) {
        String[] splits = token.split("\\.");
        String[] subSplits = Arrays.copyOfRange(splits, 1, splits.length);
        //addResults(current, splits[0], subSplits, result);
        /**
         LookupElementBuilder.create(token.base())
         .withTypeText("TypedContent")
         .withIcon(AllIcons.Nodes.Method);*/
        return new ArrayList<>();
    }

    private static void addResults(Token current, String split, String[] splits, CompletionResultSet result) {
        /**
         Token current = new RootTokens();
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
         }*/
    }

}
