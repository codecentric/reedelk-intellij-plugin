package com.esb.plugin;

public enum ESBLabel {


    RUNTIME_HOME("Runtime Path");

    private final String value;

    ESBLabel(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
