package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowSnapshot;

public abstract class AbstractComponentPropertiesRenderer implements ComponentPropertiesRenderer {

    protected final FlowSnapshot snapshot;
    protected final Module module;

    public AbstractComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        this.snapshot = snapshot;
        this.module = module;
    }
}
