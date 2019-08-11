package com.reedelk.plugin.commons;

import java.util.Stack;

public class StackUtils {

    private StackUtils() {
    }

    public static <T> void reverse(Stack<T> stack) {
        if (stack.isEmpty()) {
            return;
        }
        // Remove bottom element from stack
        T bottom = popBottom(stack);

        // Reverse everything else in stack
        reverse(stack);

        // Add original bottom element to top of stack
        stack.push(bottom);
    }

    private static <T> T popBottom(Stack<T> stack) {
        T top = stack.pop();
        if (stack.isEmpty()) {
            // If we removed the last element, return it
            return top;
        } else {
            // We didn't remove the last element, so remove the last element from what remains
            T bottom = popBottom(stack);
            // Since the element we removed in this function call isn't the bottom element,
            // add it back onto the top of the stack where it came from
            stack.push(top);
            return bottom;
        }
    }
}
