package de.codecentric.reedelk.plugin.action.openapi;

public class OpenApiConstants {

    private OpenApiConstants() {
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
    public static final String PROPERTY_SECURITY_REQUIREMENT_NAME = "name";
    public static final String PROPERTY_SECURITY_REQUIREMENT_SCOPES = "scopes";
    public static final String PROPERTY_EXAMPLE_INLINE = "inlineExample";
    public static final String PROPERTY_EXAMPLE_SUMMARY = "summary";
    public static final String PROPERTY_EXAMPLE_DESCRIPTION = "description";
    public static final String PROPERTY_EXAMPLE_EXTERNAL_VALUE = "externalValue";
    public static final String PROPERTY_EXAMPLE_VALUE = "value";


    // For a project resource the separator is the front slash '/' and not the OS
    // file separator which is OS dependent.
    public static final String PROJECT_RESOURCE_SEPARATOR = "/";

}
