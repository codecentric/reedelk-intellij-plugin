package com.esb.plugin.graph.manager;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.deserializer.SubFlowDeserializer;
import com.esb.plugin.graph.serializer.SubFlowSerializer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SubFlowGraphManager extends GraphManager {

    public SubFlowGraphManager(@NotNull Project project, @NotNull Module module, @NotNull VirtualFile graphFile, @NotNull FlowSnapshot snapshot, @NotNull FlowGraphProvider graphProvider) {
        super(project, module, graphFile, snapshot, graphProvider);
    }

    @Override
    protected String serialize(FlowGraph graph) {
        return SubFlowSerializer.serialize(graph);
    }

    @Override
    protected Optional<FlowGraph> deserialize(Module module, Document document, FlowGraphProvider graphProvider) {
        return SubFlowDeserializer.deserialize(module, document.getText(), graphProvider);
    }
}
