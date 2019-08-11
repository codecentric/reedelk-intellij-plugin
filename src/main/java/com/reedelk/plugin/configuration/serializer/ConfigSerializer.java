package com.reedelk.plugin.configuration.serializer;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.graph.serializer.AbstractSerializer;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ConfigSerializer {

    private ConfigSerializer() {
    }

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
