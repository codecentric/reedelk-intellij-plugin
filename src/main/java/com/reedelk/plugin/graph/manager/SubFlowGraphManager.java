package com.reedelk.plugin.graph.manager;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
import com.reedelk.plugin.graph.serializer.SubFlowSerializer;
import org.jetbrains.annotations.NotNull;

public class SubFlowGraphManager extends GraphManager {

    public SubFlowGraphManager(@NotNull Module module,
                               @NotNull VirtualFile managedFile,
                               @NotNull FlowSnapshot snapshot,
                               @NotNull FlowGraphProvider graphProvider) {
        super(module, managedFile, snapshot, graphProvider);
    }

    @Override
    protected String serialize(FlowGraph graph) {
        return SubFlowSerializer.serialize(graph);
    }

    @Override
    protected FlowGraph deserialize(Module module, Document document, FlowGraphProvider graphProvider) throws DeserializationError {
        return SubFlowDeserializer.deserialize(module, document.getText(), graphProvider);
    }
}
