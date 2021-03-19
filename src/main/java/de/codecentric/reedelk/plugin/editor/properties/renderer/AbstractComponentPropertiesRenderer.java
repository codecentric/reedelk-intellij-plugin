package de.codecentric.reedelk.plugin.editor.properties.renderer;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

public abstract class AbstractComponentPropertiesRenderer implements ComponentPropertiesRenderer {

    protected final FlowSnapshot snapshot;
    protected final Module module;

    public AbstractComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        this.snapshot = snapshot;
        this.module = module;
    }
}
