package com.reedelk.plugin.graph.manager;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
import com.reedelk.plugin.graph.serializer.SubFlowSerializer;
import com.reedelk.plugin.service.module.ComponentService;
import org.jetbrains.annotations.NotNull;

public class SubFlowGraphManager extends GraphManager {

    public SubFlowGraphManager(@NotNull Module module,
                               @NotNull VirtualFile managedFile,
                               @NotNull FlowSnapshot snapshot,
                               @NotNull FlowGraphProvider graphProvider,
                               @NotNull ComponentService componentService) {
        super(module, managedFile, snapshot, graphProvider, componentService);
    }

    @Override
    protected String serialize(FlowGraph graph) {
        return SubFlowSerializer.serialize(graph);
    }

    @Override
    protected FlowGraph deserialize(Module module, String text, FlowGraphProvider graphProvider) throws DeserializationError {
        return SubFlowDeserializer.deserialize(module, text, graphProvider);
    }
}
