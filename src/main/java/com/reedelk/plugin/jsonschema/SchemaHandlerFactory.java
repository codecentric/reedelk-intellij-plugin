package com.reedelk.plugin.jsonschema;

import org.everit.json.schema.*;

import java.util.HashMap;
import java.util.Map;

public class SchemaHandlerFactory {

    private static final Map<Class<? extends Schema>, SchemaHandler> SCHEMA_TYPE_HANDLER_MAP;

    static {
        Map<Class<? extends Schema>, SchemaHandler> tmp = new HashMap<>();
        tmp.put(EmptySchema.class, new EmptySchemaHandler());
        tmp.put(ObjectSchema.class, new ObjectSchemaHandler());
        tmp.put(StringSchema.class, new PrimitiveSchemaHandler());
        tmp.put(NumberSchema.class, new PrimitiveSchemaHandler());
        tmp.put(ReferenceSchema.class, new ReferenceSchemaHandler());
        SCHEMA_TYPE_HANDLER_MAP = tmp;
    }

    public static SchemaHandler get(Schema schemaType) {
        if (SCHEMA_TYPE_HANDLER_MAP.containsKey(schemaType.getClass())) {
            return SCHEMA_TYPE_HANDLER_MAP.get(schemaType.getClass());
        }
        throw new IllegalStateException("Could not find handler for schema type: " + schemaType.getClass());
    }
}
