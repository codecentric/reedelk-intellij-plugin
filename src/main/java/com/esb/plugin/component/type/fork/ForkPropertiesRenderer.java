package com.esb.plugin.component.type.fork;

import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

public class ForkPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public ForkPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

}
