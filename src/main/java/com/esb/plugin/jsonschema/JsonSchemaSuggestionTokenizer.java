package com.esb.plugin.jsonschema;

import com.esb.plugin.javascript.Type;
import com.intellij.openapi.vfs.VirtualFile;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonSchemaSuggestionTokenizer {

    private final Type DEFAULT = Type.ANY;

    public JsonSchemaSuggestionTokenizer() {
    }

    public SchemaDescriptor read(VirtualFile virtualFile) {
        try (InputStream inputStream = virtualFile.getInputStream()) {
            return findJsonSchemaTokens(inputStream);
        } catch (IOException e) {
            return new SchemaDescriptor(new ArrayList<>());
        }
    }

    private SchemaDescriptor findJsonSchemaTokens(InputStream inputStream) {
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        Schema schema = SchemaLoader.load(rawSchema);

        List<String> results = new ArrayList<>();
        if (schema instanceof ObjectSchema) {
            ObjectSchema objectSchema = (ObjectSchema) schema;
            Map<String, Schema> propertySchemas = objectSchema.getPropertySchemas();
            results.addAll(propertySchemas.keySet());
        }

        return new SchemaDescriptor(results);
    }

    public SchemaDescriptor read(String schemaPath) {
        try (InputStream inputStream = getClass().getResourceAsStream(schemaPath)) {
            return findJsonSchemaTokens(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new SchemaDescriptor(new ArrayList<>());
        }
    }


    public class SchemaDescriptor {
        private Type rootObjectType;
        private List<String> tokens;

        public SchemaDescriptor(List<String> tokens) {
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
}
