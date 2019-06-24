package com.esb.plugin.configuration.serializer;

import com.esb.plugin.commons.JsonObjectFactory;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Config;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ConfigSerializer {

    public static String serialize(ConfigMetadata configMetadata) {
        JSONObject configJsonObject = JsonObjectFactory.newJSONObject();
        configJsonObject.put(Implementor.name(), configMetadata.getFullyQualifiedName());
        configJsonObject.put(Config.id(), configMetadata.getId());
        configJsonObject.put(Config.title(), configMetadata.getTitle());
        configMetadata.keys().forEach(configKey -> {
            Object propertyValue = configMetadata.get(configKey);
            configJsonObject.put(configKey, propertyValue);
        });
        return configJsonObject.toString(AbstractSerializer.JSON_INDENT_FACTOR);
    }
}
