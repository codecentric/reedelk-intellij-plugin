package com.esb.plugin.designer.properties.renderer.node;

import com.esb.plugin.graph.GraphSnapshot;

public abstract class AbstractNodePropertiesRenderer implements NodePropertiesRenderer {

    protected final GraphSnapshot snapshot;

    public AbstractNodePropertiesRenderer(GraphSnapshot snapshot) {
        this.snapshot = snapshot;
    }


}
