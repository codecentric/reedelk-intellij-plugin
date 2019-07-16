package com.esb.plugin.jsonschema;

import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;

import java.util.List;

public class ReferenceSchemaHandler implements SchemaHandler {

    @Override
    public void handle(List<String> collector, String parent, String propertyKey, Schema propertySchema) {
        ReferenceSchema referencedSchema = (ReferenceSchema) propertySchema;
        Schema referredSchema = referencedSchema.getReferredSchema();
        SchemaHandlerFactory.get(referredSchema)
                .handle(collector, parent + propertyKey, "", referredSchema);
    }
}
