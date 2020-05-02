package com.reedelk.plugin.component.serializer;

import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static com.reedelk.plugin.graph.serializer.AbstractSerializer.JSON_INDENT_FACTOR;

public class ConfigurationSerializer {

    private ConfigurationSerializer() {
    }

    public static String serialize(@NotNull ConfigMetadata dataHolder) {

        ObjectDescriptor typeObjectDescriptor =
                dataHolder.getConfigObjectDescriptor();

        JSONObject object = JsonObjectFactory.newJSONObject();
        JsonParser.Config.id(dataHolder.getId(), object);
        JsonParser.Config.title(dataHolder.getTitle(), object);
        JsonParser.Implementor.name(dataHolder.getFullyQualifiedName(), object);

        ComponentDataHolderSerializer.get().serialize(typeObjectDescriptor, object, dataHolder);

        return object.toString(JSON_INDENT_FACTOR);
    }
}
