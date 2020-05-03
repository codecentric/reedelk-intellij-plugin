package com.reedelk.plugin.commons;

public class ComponentPropertyPath {

    private ComponentPropertyPath() {
    }

    public static String join(String first, String second) {
        return first + "#" + second;
    }
}
