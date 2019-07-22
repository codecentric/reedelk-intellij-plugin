package com.esb.plugin.jsonschema;

import org.everit.json.schema.Schema;

import java.util.List;

public class EmptySchemaHandler implements SchemaHandler {
    @Override
    public void handle(List<String> collector, String parent, String propertyKey, Schema propertySchema) {
        // nothing to do
    }
}
