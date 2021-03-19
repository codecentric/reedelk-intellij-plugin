package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiConstants;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiExampleFormat;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiUtils;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.ExampleObject;
import de.codecentric.reedelk.plugin.template.AssetProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isBlank;

public class ExampleObjectSerializer extends AbstractSerializer<ExampleObject> {

    protected ExampleObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ExampleObject exampleObject) {
        Map<String,Object> serialized = new LinkedHashMap<>();
        if (isBlank(exampleObject.getExampleRef())) {

            String data = exampleObject.getValue();
            OpenApiExampleFormat exampleFormat = context.exampleFormatOf(data);

            AssetProperties properties = new AssetProperties(data);

            String finalFileName = OpenApiUtils.exampleFileNameFrom(navigationPath, exampleFormat);

            String exampleAssetPath = context.createAsset(finalFileName, properties);

            // This is an inline schema because there is no reference.

            serialized.put(OpenApiConstants.PROPERTY_EXAMPLE_INLINE, true);
            serialized.put(OpenApiConstants.PROPERTY_EXAMPLE_SUMMARY, exampleObject.getSummary());
            serialized.put(OpenApiConstants.PROPERTY_EXAMPLE_DESCRIPTION, exampleObject.getDescription());
            serialized.put(OpenApiConstants.PROPERTY_EXAMPLE_EXTERNAL_VALUE, exampleObject.getExternalValue());
            serialized.put(OpenApiConstants.PROPERTY_EXAMPLE_VALUE, exampleAssetPath);

        } else {
            context.getAssetFrom(exampleObject.getExampleRef())
                    .ifPresent(assetPath -> serialized.put("value", assetPath));
        }

        return serialized;
    }
}
