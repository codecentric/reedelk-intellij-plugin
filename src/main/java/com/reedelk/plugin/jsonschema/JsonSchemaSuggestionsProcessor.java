package com.reedelk.plugin.jsonschema;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonSchemaSuggestionsProcessor {

    private final JSONObject jsonSchemaObject;
    private final SchemaClient client;
    private final String noParent = "";

    public JsonSchemaSuggestionsProcessor(@NotNull JSONObject jsonSchemaObject,
                                          @NotNull SchemaClient client) {
        this.jsonSchemaObject = jsonSchemaObject;
        this.client = client;
    }

    public JsonSchemaSuggestionsResult process() {
        SchemaLoader loader = SchemaLoader.builder()
                .schemaJson(jsonSchemaObject)
                .schemaClient(client)
                .build();

        Schema schema = loader.load().build();

        List<String> results = new ArrayList<>();

        SchemaHandlerFactory.get(schema)
                .handle(results, noParent, null, schema);

        return new JsonSchemaSuggestionsResult(results);
    }
}
