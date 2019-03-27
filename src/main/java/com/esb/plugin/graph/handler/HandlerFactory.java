package com.esb.plugin.graph.handler;

import com.esb.internal.commons.JsonParser;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;

import java.util.Map;

public class HandlerFactory {

    private static final DrawableComponentHandler DEFAULT = new DrawableGenericHandler();

    private static final Map<String, DrawableComponentHandler> COMPONENT_DRAWABLE_MAP = ImmutableMap.of(
            "com.esb.component.Choice", new DrawableChoiceHandler());


    public static DrawableComponentHandler get(JSONObject implementorDefinition) {
        String implementorName = JsonParser.Implementor.name(implementorDefinition);
        return COMPONENT_DRAWABLE_MAP.getOrDefault(implementorName, DEFAULT);
    }
}
