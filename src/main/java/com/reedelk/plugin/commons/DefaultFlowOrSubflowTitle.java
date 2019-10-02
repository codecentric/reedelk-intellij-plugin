package com.reedelk.plugin.commons;

public class DefaultFlowOrSubflowTitle {

    private DefaultFlowOrSubflowTitle() {
    }

    public static String from(String name) {
        return SplitWords.from(name);
    }
}
