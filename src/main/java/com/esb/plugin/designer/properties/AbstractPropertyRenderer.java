package com.esb.plugin.designer.properties;

import com.esb.plugin.graph.FlowGraph;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

public abstract class AbstractPropertyRenderer implements PropertyRenderer {

    protected final Module module;
    protected final FlowGraph graph;
    protected final VirtualFile file;

    public AbstractPropertyRenderer(final Module module, final FlowGraph graph, final VirtualFile file) {
        this.file = file;
        this.graph = graph;
        this.module = module;
    }

}
