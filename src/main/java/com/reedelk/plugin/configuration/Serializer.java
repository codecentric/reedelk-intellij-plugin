package com.reedelk.plugin.configuration;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.serialization.ComponentDataHolderSerializer;
import com.reedelk.plugin.graph.serializer.AbstractSerializer;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.json.JSONObject;

public class Serializer {

    private Serializer() {
    }

    public static String serialize(ConfigMetadata dataHolder) {
        JSONObject configJsonObject = JsonObjectFactory.newJSONObject();

        ComponentDataHolderSerializer.serialize(dataHolder, configJsonObject);

        return configJsonObject.toString(AbstractSerializer.JSON_INDENT_FACTOR);
    }
}
