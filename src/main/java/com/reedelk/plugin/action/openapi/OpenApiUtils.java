package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;

public class OpenApiUtils {

    private static final String DEFAULT_TITLE = "my_api";

    public static String configFileNameOf(OpenApiObject openApiObject) {
        String openApiName = DEFAULT_TITLE;
        if (openApiObject.getInfo() != null) {
            openApiName = openApiObject.getInfo().getTitle();
        }
        return StringUtils.isBlank(openApiName) ?
                DEFAULT_TITLE + "." + FileExtension.CONFIG.value() :
                normalize(openApiName) + "." + FileExtension.CONFIG.value();
    }

    public static String normalize(String value) {
        return value.replaceAll(" ", "_");
    }

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
}
