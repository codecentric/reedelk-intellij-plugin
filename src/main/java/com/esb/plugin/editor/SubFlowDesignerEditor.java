package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.DesignerPanelActionHandler;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.manager.GraphManager;
import com.intellij.openapi.module.Module;

public class SubFlowDesignerEditor extends FlowDesignerEditor {

    SubFlowDesignerEditor(Module module, FlowSnapshot snapshot, GraphManager manager, DesignerPanelActionHandler actionHandler) {
        super(module, snapshot, manager, actionHandler);
    }
}
