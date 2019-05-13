package com.esb.plugin.designer.properties;

import com.esb.plugin.graph.GraphSnapshot;

public abstract class AbstractPropertyRenderer implements PropertyRenderer {

    protected final GraphSnapshot snapshot;

    public AbstractPropertyRenderer(GraphSnapshot snapshot) {
        this.snapshot = snapshot;
    }

}
