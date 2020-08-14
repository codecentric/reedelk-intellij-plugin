package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.v3.model.Schema;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public enum OpenApiSchemaFormat {

    YAML("yaml"){
        @Override
        public String dump(Schema schema) {
            Map<String, Object> schemaData = schema.getSchemaData();
            return new Yaml().dump(schemaData);
        }
    },

    JSON("json"){
        @Override
        public String dump(Schema schema) {
            Map<String, Object> schemaData = schema.getSchemaData();
            return new JSONObject(schemaData).toString(2);
        }
    };

    final String extension;

    OpenApiSchemaFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public abstract String dump(Schema schema);

}
