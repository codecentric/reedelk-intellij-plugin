package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.DesignerPanelActionHandler;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.manager.GraphManager;

public class SubFlowDesignerEditor extends FlowDesignerEditor {

    SubFlowDesignerEditor(GraphSnapshot snapshot, GraphManager manager, DesignerPanelActionHandler actionHandler) {
        super(snapshot, manager, actionHandler);
    }
}
