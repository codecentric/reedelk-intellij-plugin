package com.reedelk.plugin.action.openapi;

public enum OpenApiExampleFormat {

    XML("xml"),
    JSON("json"),
    YAML("yaml"),
    PLAIN_TEXT("txt");

    private final String extension;

    OpenApiExampleFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
