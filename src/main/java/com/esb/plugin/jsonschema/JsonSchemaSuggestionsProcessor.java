package com.esb.plugin.jsonschema;

import com.esb.plugin.editor.properties.widget.input.script.ProjectFileContentProvider;
import com.intellij.openapi.module.Module;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class JsonSchemaSuggestionsProcessor {

    private final String noParent = "";

    private final Module module;
    private final String jsonSchema;
    private final String parentFolder;
    private final ProjectFileContentProvider provider;

    public JsonSchemaSuggestionsProcessor(@NotNull Module module,
                                          @NotNull String jsonSchema,
                                          @NotNull String parentFolder,
                                          @NotNull ProjectFileContentProvider provider) {
        this.module = module;
        this.provider = provider;
        this.jsonSchema = jsonSchema;
        this.parentFolder = parentFolder;
    }

    public JsonSchemaSuggestionsResult read() {
        JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchema));
        String rootId = rawSchema.getString("$id");
        int lastSlash = rootId.lastIndexOf("/");
        String rootPath = rootId.substring(0, lastSlash);

        SchemaLoader loader = SchemaLoader.builder()
                .schemaJson(rawSchema)
                .schemaClient(new JsonSchemaProjectClient(module, parentFolder, rootPath, provider))
                .build();

        Schema schema = loader.load().build();

        List<String> results = new ArrayList<>();

        SchemaHandlerFactory.get(schema)
                .handle(results, noParent, null, schema);

        return new JsonSchemaSuggestionsResult(results);
    }
}
