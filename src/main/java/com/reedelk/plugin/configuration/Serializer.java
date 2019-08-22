package com.reedelk.plugin.configuration;

import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.component.serialization.ComponentDataHolderSerializer;
import com.reedelk.plugin.graph.serializer.AbstractSerializer;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import com.reedelk.runtime.commons.JsonParser;
import org.json.JSONObject;

public class Serializer {

    private Serializer() {
    }

    public static String serialize(ConfigMetadata dataHolder) {

        TypeObjectDescriptor typeObjectDescriptor =
                dataHolder.getConfigObjectDescriptor();

        JSONObject serialize =
                ComponentDataHolderSerializer.serialize(typeObjectDescriptor, dataHolder);

        JsonParser.Config.id(dataHolder.getId(), serialize);
        JsonParser.Config.title(dataHolder.getTitle(), serialize);
        JsonParser.Implementor.name(dataHolder.getFullyQualifiedName(), serialize);
        return serialize.toString(AbstractSerializer.JSON_INDENT_FACTOR);
    }
}
