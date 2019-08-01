package com.esb.plugin.graph.manager;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.deserializer.DeserializationError;
import com.esb.plugin.graph.deserializer.SubFlowDeserializer;
import com.esb.plugin.graph.serializer.SubFlowSerializer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
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
