package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowSnapshot;

public abstract class AbstractPropertiesRenderer implements PropertiesRenderer {

    protected final FlowSnapshot snapshot;
    protected final Module module;

    public AbstractPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        this.snapshot = snapshot;
        this.module = module;
    }
}
