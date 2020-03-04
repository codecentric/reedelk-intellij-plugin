package com.reedelk.plugin.component.type.foreach;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;

public class ForEachPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public ForEachPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }
}
