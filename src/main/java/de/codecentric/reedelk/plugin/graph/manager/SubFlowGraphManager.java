package de.codecentric.reedelk.plugin.graph.manager;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphProvider;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializationError;
import de.codecentric.reedelk.plugin.graph.serializer.SubFlowSerializer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import de.codecentric.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
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
    protected FlowGraph deserialize(Module module, String text, FlowGraphProvider graphProvider) throws DeserializationError {
        return SubFlowDeserializer.deserialize(module, text, graphProvider);
    }
}
