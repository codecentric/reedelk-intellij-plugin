package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Stack;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class TokenFinder {

    private static final Logger LOG = Logger.getInstance(TokenFinder.class);

    private TokenFinder() {}

    public static Optional<String[]> find(@NotNull CompletionParameters parameters) {
        String text = parameters.getPosition().getText();
        int offset = parameters.getOffset();
        return findLastToken(text, offset);
    }

    public static Optional<String[]> findLastToken(String text, int offset) {
        try {
            return internalFindLastToken(text, offset).map(TokenNormalizer::normalize);
        } catch (Exception exception) {
            String message = message("module.completion.provider.error.token", exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }

    private static Optional<String> internalFindLastToken(String text, int offset) {
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
            // If c is ) -> We need to consume until we find the matching pair...
            if (c == ')') {
                count = consumeUntilFound(text, count - 1);
            }

            if (c == '.') {
                Optional<Integer> isBuilder = isBuilder(text, count);
                if (isBuilder.isPresent()) {
                    count = isBuilder.get() + 1;
                }
                Optional<Integer> isListIterator = isListIterator(text, count);
                if (isListIterator.isPresent()) {
                    count = isListIterator.get() + 1;
                }
            }

            // It is the end of the token
            if (c == '\n' || c == ' ' || c == '\t') {
                index = count + 1;
                break;
            }
        }

        // Represent the word starting from a space ' ' or a new line \n or the beginning of the document.
        String token = text.substring(index, offset);
        // IF it is blank, // Might be all \n\n\n up to it, hence we can't provide any suggestion.
        return StringUtils.isBlank(token) ? Optional.empty() : Optional.of(token);
    }

    private static final String matchingPattern = "collect{it";

    // message.payload().collect { it.IntellijIdeaRulezzz  }
    private static Optional<Integer> isListIterator(String text, int count) {
        int current = count - 1;
        char c = text.charAt(current);
        int patternMatchingCount = matchingPattern.length() - 1;
        boolean patternMatches = c == matchingPattern.charAt(patternMatchingCount);
        while (patternMatches && current > 0) {
            current--;
            c = text.charAt(current);
            if (c == '\n' || c == ' ' || c == '\t') {
                continue;
            }

            patternMatchingCount--;

            patternMatches = c == matchingPattern.charAt(patternMatchingCount);
            if (patternMatches && patternMatchingCount == 0) {
                // Matches.
                return Optional.of(current);
            }
        }

        return Optional.empty();
    }

    // Consumes the characters until it finds the previous end of method call e.g message.payload().super().text();
    private static Optional<Integer> isBuilder(String text, int count) {
        int current = count - 1;
        char c = text.charAt(current);
        while ((c == '\n' || c == ' ' || c == '\t') && current > 0) {
            current--;
            c = text.charAt(current);
            if (c == ')') return Optional.of(current);
        }
        return Optional.empty();
    }

    private static int consumeUntilFound(String text, int count) {
        Stack<Character> stack = new Stack<>();
        stack.push(')');
        while (!stack.isEmpty() && count > 1) {
            char c = text.charAt(count);
            if (c == '(') stack.pop();
            if (c == ')') stack.push(')');
            count--;
        }
        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("The parenthesis in the text were not balanced and I could not find the end of the token.");
        } else {
            return count;
        }
    }
}
