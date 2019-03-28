package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;

import java.util.Map;

import static com.esb.plugin.commons.SystemComponents.*;

public class BuilderFactory {

    private static final Builder DEFAULT = new GenericDrawableBuilder();
    private static final Map<String, Builder> COMPONENT_DRAWABLE_MAP = ImmutableMap.of(
            CHOICE.qualifiedName(), new ChoiceDrawableBuilder(),
            FORK.qualifiedName(), new ForkJoinDrawableBuilder(),
            FLOW_REFERENCE.qualifiedName(), new FlowReferenceDrawableBuilder());

    public static Builder get(JSONObject implementorDefinition) {
        String implementorName = JsonParser.Implementor.name(implementorDefinition);
        return COMPONENT_DRAWABLE_MAP.getOrDefault(implementorName, DEFAULT);
    }
}
