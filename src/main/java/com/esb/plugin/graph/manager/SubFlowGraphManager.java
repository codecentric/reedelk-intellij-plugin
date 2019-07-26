package com.esb.plugin.graph.manager;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.deserializer.SubFlowDeserializer;
import com.esb.plugin.graph.serializer.SubFlowSerializer;
import com.esb.plugin.service.project.SelectableItem;
import com.esb.plugin.service.project.SelectableItemSubflow;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SubFlowGraphManager extends GraphManager {

    private final SelectableItem nothingSelectedItem;

    public SubFlowGraphManager(@NotNull Module module,
                               @NotNull VirtualFile managedFile,
                               @NotNull FlowSnapshot snapshot,
                               @NotNull FlowGraphProvider graphProvider) {
        super(module, managedFile, snapshot, graphProvider);
        nothingSelectedItem = new SelectableItemSubflow(snapshot);
    }

    @Override
    protected String serialize(FlowGraph graph) {
        return SubFlowSerializer.serialize(graph);
    }

    @Override
    protected Optional<FlowGraph> deserialize(Module module, Document document, FlowGraphProvider graphProvider) {
        return SubFlowDeserializer.deserialize(module, document.getText(), graphProvider);
    }

    @Override
    protected SelectableItem getNothingSelectedItem() {
        return nothingSelectedItem;
    }
}
