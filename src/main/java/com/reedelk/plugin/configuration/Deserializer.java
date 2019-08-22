package com.reedelk.plugin.configuration;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.component.serialization.ComponentDataHolderDeserializer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class Deserializer {

    private static final Logger LOG = Logger.getInstance(Deserializer.class);

    private Deserializer() {
    }

    public static Optional<ComponentDataHolder> deserialize(String json, TypeObjectDescriptor configTypeDescriptor) {
        JSONObject jsonDefinition;
        try {
            jsonDefinition = new JSONObject(json);
        } catch (JSONException e) {
            LOG.warn(e);
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
    private static Optional<ComponentDataHolder> deserialize(TypeObjectDescriptor propertyType, JSONObject jsonDefinition) {
        TypeObjectDescriptor.TypeObject dataHolder = propertyType.newInstance();
        dataHolder.set(Config.id(), jsonDefinition.getString(Config.id()));
        dataHolder.set(Config.title(), jsonDefinition.getString(Config.title()));
        dataHolder.set(Implementor.name(), jsonDefinition.getString(Implementor.name()));
        propertyType.getObjectProperties().forEach(descriptor ->
                ComponentDataHolderDeserializer.deserialize(jsonDefinition, dataHolder, descriptor));
        return Optional.of(dataHolder);

    }

}
