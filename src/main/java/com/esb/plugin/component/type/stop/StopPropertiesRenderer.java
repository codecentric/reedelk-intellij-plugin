package com.esb.plugin.component.type.stop;

import com.esb.plugin.editor.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

public class StopPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public StopPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

}
