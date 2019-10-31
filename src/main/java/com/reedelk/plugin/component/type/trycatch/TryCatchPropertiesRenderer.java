package com.reedelk.plugin.component.type.trycatch;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;

public class TryCatchPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public TryCatchPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }
}
