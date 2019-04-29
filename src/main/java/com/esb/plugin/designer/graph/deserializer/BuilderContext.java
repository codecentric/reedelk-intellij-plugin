package com.esb.plugin.designer.graph.deserializer;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;

public class BuilderContext {

    private final Module module;

    public BuilderContext(Module module) {
        this.module = module;
    }

    Component instantiateComponent(String componentName) {
        ComponentDescriptor descriptor = ComponentService.getInstance(module)
                .componentDescriptorByName(componentName);
        return new Component(descriptor);
    }
}
