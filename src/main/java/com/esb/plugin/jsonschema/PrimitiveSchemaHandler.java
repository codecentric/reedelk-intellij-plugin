package com.esb.plugin.jsonschema;

import com.esb.internal.commons.StringUtils;
import org.everit.json.schema.Schema;

import java.util.List;

public class PrimitiveSchemaHandler implements SchemaHandler {

    @Override
    public void handle(List<String> collector, String parent, String propertyKey, Schema propertySchema) {
        if (StringUtils.isBlank(parent)) {
            collector.add(propertyKey);
        } else {
            collector.add(parent + propertyKey);
        }
    }
}
