package de.codecentric.reedelk.plugin.component.type.foreach;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;

public class ForEachPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public ForEachPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }
}
