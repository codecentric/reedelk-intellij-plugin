package com.esb.plugin;

public enum ESBLabel {

    GROUP_ID("GroupId"),
    VERSION("Version"),
    ARTIFACT_ID("ArtifactId"),
    RUNTIME_HOME("Runtime Home");

    private final String value;

    ESBLabel(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
