package com.esb.plugin.graph.deserializer;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.graph.FlowGraph;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class GraphDeserializer {

    public static Optional<FlowGraph> deserialize(Module module, VirtualFile file) {
        try {
            String json = FileUtils.readFrom(new URL(file.getUrl()));
            return deserialize(module, json);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    public static Optional<FlowGraph> deserialize(Module module, String json) {
        try {
            BuilderContext context = new BuilderContext(module);
            GraphBuilder builder = new GraphBuilder(json, context);
            FlowGraph graph = builder.deserialize();
            return Optional.of(graph);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
