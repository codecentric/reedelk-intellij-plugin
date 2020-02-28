package com.reedelk.plugin.service.module.impl.completion;

import java.util.Stack;

public class InputTokenizer {

    private InputTokenizer() {}

    public static String[] tokenize(String input) {
        StringBuilder builder = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                stack.pop();
            } else if (stack.empty() && c != ';') {
                builder.append(c);
            }
        }

        // If stack is not empty
        if (!stack.isEmpty()) {
            // We need to go back
            int i = input.length() - 1;
            while (i > 0) {
                char c1 = input.charAt(i);
                if (c1 == '(') {
                    return input.substring(i+1).split("\\.");
                }
                i--;
            }
        }

        return builder.toString().split("\\.");
    }
}
