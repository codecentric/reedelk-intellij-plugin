package com.reedelk.plugin.action.openapi;

public enum OpenApiSchemaFormat {

    YAML("yaml"),
    JSON("json");

    final String extension;

    OpenApiSchemaFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static OpenApiSchemaFormat formatOf(String fileName) {
        for (OpenApiSchemaFormat schemaFormat : OpenApiSchemaFormat.values()) {
            if (fileName.toLowerCase().endsWith("." + schemaFormat.extension)) {
                return schemaFormat;
            }
        }
        throw new IllegalArgumentException("Could not find schema format for file with name=[" + fileName + "]");
    }
}
