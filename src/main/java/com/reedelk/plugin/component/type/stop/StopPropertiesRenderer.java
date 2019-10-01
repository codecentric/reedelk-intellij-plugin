package com.reedelk.plugin.component.type.stop;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;

public class StopPropertiesRenderer extends AbstractPropertiesRenderer {

    public StopPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

}
