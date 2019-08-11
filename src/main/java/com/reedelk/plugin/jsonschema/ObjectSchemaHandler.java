package com.reedelk.plugin.jsonschema;

import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;

import java.util.List;
import java.util.Map;

public class ObjectSchemaHandler implements SchemaHandler {

    @Override
    public void handle(List<String> collector, String parent, String propertyKey, Schema propertySchema) {

        ObjectSchema objectSchema = (ObjectSchema) propertySchema;
        for (Map.Entry<String, Schema> property : objectSchema.getPropertySchemas().entrySet()) {
            String key = property.getKey();
            Schema pSchema = property.getValue();

            String realParent = parent == null || parent.equals("") ? "" : parent + ".";

            SchemaHandlerFactory.get(pSchema)
                    .handle(collector, realParent, key, pSchema);
        }
    }
}
