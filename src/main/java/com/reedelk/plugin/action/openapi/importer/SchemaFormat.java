package com.reedelk.plugin.action.openapi.importer;

public enum SchemaFormat {

    YAML("yaml"),
    JSON("json");

    final String extension;

    SchemaFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static SchemaFormat formatOf(String fileName) {
        for (SchemaFormat schemaFormat : SchemaFormat.values()) {
            if (fileName.toLowerCase().endsWith("." + schemaFormat.extension)) {
                return schemaFormat;
            }
        }
        throw new IllegalArgumentException("Could not find schema format for file with name=[" + fileName + "]");
    }
}
