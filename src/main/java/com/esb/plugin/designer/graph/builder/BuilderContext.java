package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;

public class BuilderContext {

    private final Module module;

    public BuilderContext(Module module) {
        this.module = module;
    }

    public ComponentDescriptor instantiateComponent(String componentName) {
        return ComponentService.getInstance(module)
                .componentDescriptorByName(componentName);
    }
}
