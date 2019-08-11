package com.reedelk.plugin.jsonschema;

import com.reedelk.runtime.commons.StringUtils;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;

import java.util.List;

public class ReferenceSchemaHandler implements SchemaHandler {

    @Override
    public void handle(List<String> collector, String parent, String propertyKey, Schema propertySchema) {
        if (StringUtils.isBlank(parent)) {
            collector.add(propertyKey);
        } else {
            collector.add(parent + propertyKey);
        }

        ReferenceSchema referencedSchema = (ReferenceSchema) propertySchema;

        Schema referredSchema = referencedSchema.getReferredSchema();

        SchemaHandlerFactory.get(referredSchema)
                .handle(collector, parent + propertyKey, "", referredSchema);
    }
}
