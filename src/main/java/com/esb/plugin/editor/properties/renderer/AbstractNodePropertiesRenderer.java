package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

public abstract class AbstractNodePropertiesRenderer implements NodePropertiesRenderer {

    protected final FlowSnapshot snapshot;
    protected final Module module;

    public AbstractNodePropertiesRenderer(FlowSnapshot snapshot, Module module) {
        this.snapshot = snapshot;
        this.module = module;
    }
}
