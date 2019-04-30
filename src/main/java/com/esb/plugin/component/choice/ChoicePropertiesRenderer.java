package com.esb.plugin.component.choice;

import com.esb.plugin.component.Component;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;

public class ChoicePropertiesRenderer extends AbstractPropertyRenderer {

    public ChoicePropertiesRenderer(Module module, FlowGraph graph, VirtualFile file) {
        super(module, graph, file);
    }

    @Override
    public void render(JBPanel panel, Component component) {

    }
}
