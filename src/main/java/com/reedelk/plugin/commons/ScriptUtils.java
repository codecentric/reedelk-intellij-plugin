package com.reedelk.plugin.commons;

public class ScriptUtils {

    public static boolean isScript(String value) {
        return value.startsWith("#[") && value.endsWith("]");
    }

    // Remove:#[ and ] from the  beginning and the end of the text
    public static String unwrap(String value) {
        return value.substring(2, value.length() - 1);
    }

    public static String asScript(String text) {
        return "#[" + text + "]";
    }
}