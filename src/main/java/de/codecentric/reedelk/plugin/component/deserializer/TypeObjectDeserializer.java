package de.codecentric.reedelk.plugin.component.deserializer;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.Shared;
import de.codecentric.reedelk.plugin.commons.TypeObjectFactory;
import de.codecentric.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

class TypeObjectDeserializer {

    private static final TypeObjectDeserializer INSTANCE = new TypeObjectDeserializer();

    static TypeObjectDeserializer get() {
        return INSTANCE;
    }

    void deserialize(@NotNull JSONObject jsonObject,
                     @NotNull ComponentDataHolder componentData,
                     @NotNull String propertyName,
                     @NotNull ObjectDescriptor propertyType) {

        if (jsonObject.has(propertyName) && !jsonObject.isNull(propertyName)) {

            ObjectDescriptor.TypeObject nestedObject = TypeObjectFactory.newInstance(propertyType);

            JSONObject nestedJsonObject = jsonObject.getJSONObject(propertyName);

            // If the property is present in the JSON but it is an empty object we
            // still fill up instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            // This happens for instance when we have a JSON object like the following:
            // "configuration": {}
            if (nestedJsonObject.isEmpty()) {
                addEmptyInstances(componentData, propertyName, propertyType);

            } else if (Shared.YES.equals(propertyType.getShared())) {
                // The config is shareable, therefore we just set the
                // reference value pointing to the shared config.
                if (nestedJsonObject.has(JsonParser.Component.ref())) {
                    String reference = JsonParser.Component.ref(nestedJsonObject);
                    nestedObject.set(JsonParser.Component.ref(), reference);
                    componentData.set(propertyName, nestedObject);
                } else {
                    // The nested JSON object does not have a component "ref" property inside it.
                    // We still must add empty instances for the type object.
                    addEmptyInstances(componentData, propertyName, propertyType);
                }
            } else {
                // The config is not shareable, hence we deserialize the object right away.
                propertyType.getObjectProperties().forEach(typeDescriptor ->
                        ComponentDataHolderDeserializer.get().deserialize(nestedObject, typeDescriptor, nestedJsonObject));
                componentData.set(propertyName, nestedObject);
            }

        } else {
            // If the property is not present in the JSON we still fill up
            // Instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            addEmptyInstances(componentData, propertyName, propertyType);
        }
    }

    private void addEmptyInstances(ComponentDataHolder dataHolder, String propertyName, PropertyTypeDescriptor descriptor) {
        if (descriptor instanceof ObjectDescriptor) {
            ObjectDescriptor propertyObjectType = (ObjectDescriptor) descriptor;
            ObjectDescriptor.TypeObject typeObject = TypeObjectFactory.newInstance(propertyObjectType);

            dataHolder.set(propertyName, typeObject);
            // From now on, the subtree contains null objects.
            propertyObjectType.getObjectProperties()
                    .forEach(propertyDescriptor ->
                            addEmptyInstances(typeObject, propertyDescriptor.getName(), propertyDescriptor.getType()));
        }
    }
}
