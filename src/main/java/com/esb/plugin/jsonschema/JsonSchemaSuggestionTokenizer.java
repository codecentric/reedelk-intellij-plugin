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
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class JsonSchemaSuggestionTokenizer {

    private final Type DEFAULT = Type.OBJECT;
    private final Module module;

    public JsonSchemaSuggestionTokenizer(Module module) {
        this.module = module;
    }

    public SchemaDescriptor read(String parent, VirtualFile virtualFile) {
        try (InputStream inputStream = virtualFile.getInputStream()) {
            return findJsonSchemaTokens(parent, inputStream);
        } catch (IOException e) {
            return new SchemaDescriptor(new ArrayList<>());
        }
    }

    SchemaDescriptor findJsonSchemaTokens(String parent, InputStream inputStream) {
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        SchemaLoader loader = SchemaLoader.builder()
                .schemaJson(rawSchema)
                .schemaClient(new ProjectClassPathClient(module))
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

        private static final List<String> HANDLED_PREFIXES = unmodifiableList(asList("classpath://", "classpath:/", "classpath:"));

        private final SchemaClient fallbackClient;
        private Module module;

        ProjectClassPathClient(@NotNull Module module) {
            this.fallbackClient = new DefaultSchemaClient();
            this.module = module;
        }

        @Override
        public InputStream get(String url) {
            return ModuleUtils.getResourcesFolder(module)
                    .map(resourcesFolder -> loadFromResources(resourcesFolder, url))
                    .orElseGet(() -> fallbackClient.get(url));
        }

        @Nullable
        private InputStream loadFromResources(String resourcesFolder, String url) {
            Path path = Paths.get(resourcesFolder, url);
            try {
                return new FileInputStream(path.toFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        private InputStream loadFromClasspath(String str) {

            return getClass().getResourceAsStream(str);
        }

        private Optional<String> handleProtocol(String url) {
            return HANDLED_PREFIXES.stream().filter(url::startsWith)
                    .map(prefix -> "/" + url.substring(prefix.length()))
                    .findFirst();
        }
    }
}
