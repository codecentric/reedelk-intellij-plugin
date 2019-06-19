package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

import javax.swing.*;

public interface TypePropertyRenderer {

    JComponent render(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot);

}
