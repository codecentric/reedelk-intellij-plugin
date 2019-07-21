package com.esb.plugin.jsonschema;

import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.editor.properties.widget.input.script.ProjectFileContentProvider;
import com.esb.plugin.javascript.Type;
import com.intellij.openapi.module.Module;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.loader.internal.DefaultSchemaClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonSchemaSuggestionProcessor {

    private final Type DEFAULT = Type.OBJECT;
    private final Module module;
    private final String jsonSchema;
    private final String parentFolder;
    private final ProjectFileContentProvider provider;

    public JsonSchemaSuggestionProcessor(@NotNull Module module,
                                         @NotNull String jsonSchema,
                                         @NotNull String parentFolder,
                                         @NotNull ProjectFileContentProvider provider) {
        this.module = module;
        this.provider = provider;
        this.jsonSchema = jsonSchema;
        this.parentFolder = parentFolder;
    }

    public SchemaDescriptor read(String parent) {
        JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchema));
        String rootId = rawSchema.getString("$id");
        int lastSlash = rootId.lastIndexOf("/");
        String rootPath = rootId.substring(0, lastSlash);

        SchemaLoader loader = SchemaLoader.builder()
                .schemaJson(rawSchema)
                .schemaClient(new ProjectClassPathClient(module, parentFolder, rootPath, provider))
                .build();

        Schema schema = loader.load().build();

        List<String> results = new ArrayList<>();

        SchemaHandlerFactory.get(schema)
                .handle(results, parent, null, schema);

        return new SchemaDescriptor(results);
    }

    public class SchemaDescriptor {
        private Type rootObjectType;
        private List<String> tokens;

        SchemaDescriptor(List<String> tokens) {
            this.rootObjectType = DEFAULT;
            this.tokens = tokens;
        }

        public Type getType() {
            return rootObjectType;
        }

        public List<String> getTokens() {
            return tokens;
        }
    }

    static class ProjectClassPathClient implements SchemaClient {

        private final SchemaClient fallbackClient;
        private final String parentFolder;
        private final String rootHostPath;
        private final Module module;
        private final ProjectFileContentProvider provider;

        ProjectClassPathClient(@NotNull Module module,
                               @NotNull String parentFolder,
                               @NotNull String rootHostPath,
                               @NotNull ProjectFileContentProvider provider) {
            this.fallbackClient = new DefaultSchemaClient();
            this.parentFolder = parentFolder;
            this.rootHostPath = rootHostPath;
            this.provider = provider;
            this.module = module;
        }

        @Override
        public InputStream get(String url) {
            if (url.startsWith(rootHostPath)) {
                String relative = url.substring(rootHostPath.length());
                String finalFilePath = parentFolder + relative;
                String content = provider.getContent(finalFilePath);
                return new ByteArrayInputStream(content.getBytes());
            }

            return ModuleUtils.getResourcesFolder(module)
                    .map(resourcesFolder -> loadFromResources(resourcesFolder, url))
                    .orElseGet(() -> fallbackClient.get(url));
        }

        private InputStream loadFromResources(String resourcesFolder, String url) {
            String finalFilePath = resourcesFolder + url;
            String content = provider.getContent(finalFilePath);
            return new ByteArrayInputStream(content.getBytes());
        }
    }
}
