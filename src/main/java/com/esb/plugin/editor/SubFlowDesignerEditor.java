package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.DesignerPanelActionHandler;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.manager.GraphManager;

public class SubFlowDesignerEditor extends FlowDesignerEditor {

    SubFlowDesignerEditor(FlowSnapshot snapshot, GraphManager manager, DesignerPanelActionHandler actionHandler) {
        super(snapshot, manager, actionHandler);
    }
}
