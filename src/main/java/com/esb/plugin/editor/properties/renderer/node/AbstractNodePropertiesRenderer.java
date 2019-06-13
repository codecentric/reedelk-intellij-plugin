package com.esb.plugin.editor.properties.renderer.node;

import com.esb.plugin.graph.FlowSnapshot;

public abstract class AbstractNodePropertiesRenderer implements NodePropertiesRenderer {

    protected final FlowSnapshot snapshot;

    public AbstractNodePropertiesRenderer(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
    }

}
