package com.esb.plugin.service.project;

import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;

import java.util.Optional;

public interface SelectedComponentManager {
    static SelectedComponentManager getInstance() {
        return ServiceManager.getService(SelectedComponentManager.class);
    }

    Optional<CurrentSelection> getCurrentSelection();

    interface CurrentSelection {

        Module getModule();

        FlowSnapshot getSnapshot();

        GraphNode getSelectedNode();
    }
}
