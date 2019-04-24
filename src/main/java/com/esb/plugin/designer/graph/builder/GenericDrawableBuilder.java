package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import org.json.JSONObject;

public class GenericDrawableBuilder implements Builder {

    @Override
    public Drawable build(Module module, Drawable parent, JSONObject componentDefinition, FlowGraph graph) {

        String name = JsonParser.Implementor.name(componentDefinition);

        ComponentDescriptor component = ComponentService.getInstance(module).componentDescriptorByName(name);

        GenericComponentDrawable genericComponentDrawable = new GenericComponentDrawable(component);

        graph.add(parent, genericComponentDrawable);

        return genericComponentDrawable;
    }
}
