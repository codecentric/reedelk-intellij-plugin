package com.esb.plugin.service.project.impl;

import com.esb.plugin.editor.designer.ComponentSelectedListener;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.service.project.SelectedComponentManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SelectedComponentManagerImpl implements SelectedComponentManager, ComponentSelectedListener, Disposable {

    private final MessageBusConnection connection;

    private CurrentSelection currentSelection;

    public SelectedComponentManagerImpl(@NotNull Project project) {
        this.connection = project.getMessageBus().connect();
        this.connection.subscribe(ComponentSelectedListener.COMPONENT_SELECTED_TOPIC, this);
    }

    @Override
    public void onComponentUnSelected() {
        this.currentSelection = null;
    }

    @Override
    public void onComponentSelected(Module module, FlowSnapshot snapshot, GraphNode selected) {
        this.currentSelection = new CurrentSelection(module, snapshot, selected);
    }

    @Override
    public Optional<SelectedComponentManager.CurrentSelection> getCurrentSelection() {
        return Optional.ofNullable(currentSelection);
    }

    @Override
    public void dispose() {
        this.connection.disconnect();
    }

    class CurrentSelection implements SelectedComponentManager.CurrentSelection {
        private Module module;
        private GraphNode selected;
        private FlowSnapshot snapshot;

        CurrentSelection(@NotNull Module module,
                         @NotNull FlowSnapshot snapshot,
                         @NotNull GraphNode selected) {
            this.module = module;
            this.snapshot = snapshot;
            this.selected = selected;
        }

        @Override
        public Module getModule() {
            return module;
        }

        @Override
        public FlowSnapshot getSnapshot() {
            return snapshot;
        }

        @Override
        public GraphNode getSelectedNode() {
            return selected;
        }
    }
}
