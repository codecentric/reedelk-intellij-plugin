package de.codecentric.reedelk.plugin.graph.manager;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphProvider;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializationError;
import de.codecentric.reedelk.plugin.graph.serializer.FlowSerializer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import de.codecentric.reedelk.plugin.graph.deserializer.FlowDeserializer;
import org.jetbrains.annotations.NotNull;

public class FlowGraphManager extends GraphManager {

    public FlowGraphManager(@NotNull Module module,
                            @NotNull VirtualFile managedFile,
                            @NotNull FlowSnapshot snapshot,
                            @NotNull FlowGraphProvider graphProvider) {
        super(module, managedFile, snapshot, graphProvider);
    }

    @Override
    protected String serialize(FlowGraph graph) {
        return FlowSerializer.serialize(graph);
    }

    @Override
    protected FlowGraph deserialize(Module module, String text, FlowGraphProvider graphProvider) throws DeserializationError {
        return FlowDeserializer.deserialize(module, text, graphProvider);
    }
}
