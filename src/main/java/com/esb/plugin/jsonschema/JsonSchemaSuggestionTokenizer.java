package com.esb.plugin.jsonschema;

import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.javascript.Type;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.loader.internal.DefaultSchemaClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonSchemaSuggestionTokenizer {

    private final Type DEFAULT = Type.OBJECT;
    private final Module module;
    private final VirtualFile jsonSchema;
    private final String parentFolder;

    public JsonSchemaSuggestionTokenizer(@NotNull Module module, @NotNull VirtualFile jsonSchema) {
        this.module = module;
        this.jsonSchema = jsonSchema;
        this.parentFolder = jsonSchema.getParent().getPath();
    }

    public SchemaDescriptor read(String parent) {
        try (InputStream inputStream = jsonSchema.getInputStream()) {
            return findJsonSchemaTokens(parent, inputStream);
        } catch (IOException e) {
            return new SchemaDescriptor(new ArrayList<>());
        }
    }

    SchemaDescriptor findJsonSchemaTokens(String parent, InputStream inputStream) {
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        String rootId = rawSchema.getString("$id");
        int lastSlash = rootId.lastIndexOf("/");
        String rootPath = rootId.substring(0, lastSlash);

        SchemaLoader loader = SchemaLoader.builder()
                .schemaJson(rawSchema)
                .schemaClient(new ProjectClassPathClient(module, parentFolder, rootPath))
                .build();

        Schema schema = loader.load().build();

        List<String> results = new ArrayList<>();
        results.add(parent);

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

        ProjectClassPathClient(@NotNull Module module, @NotNull String parentFolder, @NotNull String rootHostPath) {
            this.fallbackClient = new DefaultSchemaClient();
            this.parentFolder = parentFolder;
            this.rootHostPath = rootHostPath;
            this.module = module;
        }

        @Override
        public InputStream get(String url) {
            if (url.startsWith(rootHostPath)) {
                String relative = url.substring(rootHostPath.length());
                Path path = Paths.get(parentFolder, relative);
                try {
                    return new FileInputStream(path.toFile());
                } catch (FileNotFoundException e) {
                    throw new UncheckedIOException(e);
                }
            }

            return ModuleUtils.getResourcesFolder(module)
                    .map(resourcesFolder -> loadFromResources(resourcesFolder, url))
                    .orElseGet(() -> fallbackClient.get(url));
        }

        private InputStream loadFromResources(String resourcesFolder, String url) {
            Path path = Paths.get(resourcesFolder, url);
            try {
                return new FileInputStream(path.toFile());
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
