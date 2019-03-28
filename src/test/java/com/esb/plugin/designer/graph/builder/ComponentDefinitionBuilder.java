package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ComponentDefinitionBuilder {

    public static JSONArray createNextComponentsArray(String... componentsNames) {
        JSONArray nextComponents = new JSONArray();
        for (String componentName : componentsNames) {
            nextComponents.put(ComponentDefinitionBuilder.forComponent(componentName)
                    .with("stringProperty", "Test")
                    .with("longProperty", 3L)
                    .build());
        }
        return nextComponents;
    }

    public static Builder forComponent(String componentName) {
        Builder builder = new Builder();
        builder.with(JsonParser.Implementor.name(), componentName);
        return builder;
    }

    public static class Builder {

        private Map<String, Object> componentProperties = new HashMap<>();

        public Builder with(String propertyName, Object propertyValue) {
            componentProperties.put(propertyName, propertyValue);
            return this;
        }

        public JSONObject build() {
            JSONObject componentDefinition = new JSONObject();
            for (Map.Entry<String, Object> entry : componentProperties.entrySet()) {
                componentDefinition.put(entry.getKey(), entry.getValue());
            }
            return componentDefinition;
        }

    }
}
