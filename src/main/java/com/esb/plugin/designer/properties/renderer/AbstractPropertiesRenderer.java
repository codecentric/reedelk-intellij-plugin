package com.esb.plugin.designer.properties.renderer;

import com.esb.plugin.graph.GraphSnapshot;

public abstract class AbstractPropertiesRenderer implements PropertiesRenderer {

    protected final GraphSnapshot snapshot;

    public AbstractPropertiesRenderer(GraphSnapshot snapshot) {
        this.snapshot = snapshot;
    }


}
