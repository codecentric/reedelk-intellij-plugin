package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ParameterObject;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Properties;

class ParameterObjectSerializer extends com.reedelk.openapi.v3.serializer.ParameterObjectSerializer {

    private final OpenApiImporterContext context;

    public ParameterObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ParameterObject input) {
        Map<String, Object> serialize = super.serialize(serializerContext, navigationPath, input);
        if (input.getSchema() != null) {
            Properties properties = new Properties();
            properties.put("schema", new Yaml().dump(input.getSchema().getSchemaData()));  // TODO: Might be JSON instead of YAML

            String finalFileName = OpenApiUtils.parameterSchemaFileNameFrom(navigationPath, context);
            String schemaAssetPath = context.createSchema(finalFileName, properties);
            serialize.put("schema", schemaAssetPath);

        }
        return serialize;
    }
}
