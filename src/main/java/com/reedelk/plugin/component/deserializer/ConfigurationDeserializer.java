package com.reedelk.plugin.component.deserializer;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.plugin.commons.TypeObjectFactory;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ConfigurationDeserializer {

    private static final Logger LOG = Logger.getInstance(ConfigurationDeserializer.class);

    private ConfigurationDeserializer() {
    }

    public static Optional<ComponentDataHolder> deserialize(String json, ObjectDescriptor configTypeDescriptor) {
        JSONObject jsonDefinition;
        try {
            jsonDefinition = new JSONObject(json);
        } catch (JSONException exception) {
            LOG.warn(exception);
            return Optional.empty();
        }

        if (jsonDefinition.has(Implementor.name())) {
            String fullyQualifiedName = Implementor.name(jsonDefinition);
            // If this config does not match the implementor
            // name of the property descriptor, we skip it.
            if (configTypeDescriptor.getTypeFullyQualifiedName().equals(fullyQualifiedName)) {
                return deserialize(configTypeDescriptor, jsonDefinition);
            }
        }

        return Optional.empty();
    }

    @NotNull
    private static Optional<ComponentDataHolder> deserialize(ObjectDescriptor propertyType, JSONObject jsonDefinition) {
        ObjectDescriptor.TypeObject dataHolder = TypeObjectFactory.newInstance(propertyType);
        dataHolder.set(Config.id(), Config.id(jsonDefinition));
        dataHolder.set(Implementor.name(), Implementor.name(jsonDefinition));
        if (jsonDefinition.has(Config.title())) {
            dataHolder.set(Config.title(), Config.title(jsonDefinition));
        }

        propertyType.getObjectProperties().forEach(descriptor ->
                ComponentDataHolderDeserializer.get().deserialize(dataHolder, descriptor, jsonDefinition));

        return Optional.of(dataHolder);
    }
}
