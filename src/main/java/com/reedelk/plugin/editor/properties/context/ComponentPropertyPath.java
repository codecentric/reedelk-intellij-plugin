package com.reedelk.plugin.editor.properties.context;

public class ComponentPropertyPath {

    private ComponentPropertyPath() {
    }

    public static String join(String first, String second) {
        return first + "#" + second;
    }
}
