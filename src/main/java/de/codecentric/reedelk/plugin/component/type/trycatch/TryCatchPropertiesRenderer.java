package de.codecentric.reedelk.plugin.component.type.trycatch;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;

public class TryCatchPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public TryCatchPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }
}
