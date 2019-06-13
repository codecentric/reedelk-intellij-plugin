package com.esb.plugin.editor;

import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.manager.GraphManager;
import com.intellij.openapi.module.Module;

public class SubFlowDesignerEditor extends FlowDesignerEditor {

    SubFlowDesignerEditor(Module module, GraphSnapshot snapshot, GraphManager manager) {
        super(module, snapshot, manager);
    }
}
