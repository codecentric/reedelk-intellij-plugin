package de.codecentric.reedelk.plugin.component.type.fork;

import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

public class ForkPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public ForkPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

}
