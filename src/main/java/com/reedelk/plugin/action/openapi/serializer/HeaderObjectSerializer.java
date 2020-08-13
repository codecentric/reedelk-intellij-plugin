package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.HeaderObject;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

class HeaderObjectSerializer extends com.reedelk.openapi.v3.serializer.HeaderObjectSerializer {

    private final OpenApiImporterContext context;

    public HeaderObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, HeaderObject input) {
        Map<String, Object> serialize = super.serialize(serializerContext, navigationPath, input);
        if (input.getSchema() != null) {
            String data = new Yaml().dump(input.getSchema().getSchemaData()); // TODO: Might be JSON instead of YAML

            String finalFileName = OpenApiUtils.headerSchemaFileNameFrom(navigationPath, context);
            String schemaAssetPath = context.createAsset(finalFileName, data);
            serialize.put(HeaderObject.Properties.SCHEMA.value(), schemaAssetPath);
        }
        return serialize;
    }
}
