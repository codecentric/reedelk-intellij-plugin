package com.esb.plugin.designer.graph.builder;

import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.internal.commons.JsonParser;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;

import java.util.Map;

public class BuilderFactory {

    private static final Builder DEFAULT = new GenericDrawableBuilder();
    private static final Map<String, Builder> COMPONENT_DRAWABLE_MAP = ImmutableMap.of(
            Choice.class.getName(), new ChoiceDrawableBuilder(),
            Fork.class.getName(), new ForkJoinDrawableBuilder(),
            FlowReference.class.getName(), new FlowReferenceDrawableBuilder());

    public static Builder get(JSONObject implementorDefinition) {
        String implementorName = JsonParser.Implementor.name(implementorDefinition);
        return COMPONENT_DRAWABLE_MAP.getOrDefault(implementorName, DEFAULT);
    }
}
