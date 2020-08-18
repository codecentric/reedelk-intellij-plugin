package com.reedelk.plugin.action.openapi;

public class OpenApiStringConstants {

    private OpenApiStringConstants() {
    }

    // Reedelk REST Listener only property. This is not part of the OpenAPI specification.
    // Be careful changing these! They must match the name of the REST Listener's:
    // - ParameterObject
    // - HeaderObject
    // - MediaTypeObject.
    public static final String PROPERTY_INLINE_SCHEMA = "inlineSchema";
    public static final String PROPERTY_PREDEFINED_SCHEMA = "predefinedSchema";
    public static final String PROPERTY_PREDEFINED_SCHEMA_NONE = "NONE";
    public static final String PROPERTY_ENUM_VALUES = "enumValues";
    public static final String PROPERTY_DEFAULT_VALUE  = "defaultValue";

}
