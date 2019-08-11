package com.reedelk.plugin.jsonschema;

import org.everit.json.schema.Schema;

import java.util.List;

public interface SchemaHandler {

    void handle(List<String> collector, String parent, String propertyKey, Schema propertySchema);
}
