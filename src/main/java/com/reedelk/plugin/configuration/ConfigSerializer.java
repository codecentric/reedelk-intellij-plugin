package com.reedelk.plugin.configuration;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.type.generic.GenericComponentSerializer;
import com.reedelk.plugin.graph.serializer.AbstractSerializer;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.json.JSONObject;

public class ConfigSerializer {

    private ConfigSerializer() {
    }

    public static String serialize(ConfigMetadata configMetadata) {
        JSONObject configJsonObject = JsonObjectFactory.newJSONObject();
        GenericComponentSerializer.serialize(configMetadata, configJsonObject);
        return configJsonObject.toString(AbstractSerializer.JSON_INDENT_FACTOR);
    }
}
