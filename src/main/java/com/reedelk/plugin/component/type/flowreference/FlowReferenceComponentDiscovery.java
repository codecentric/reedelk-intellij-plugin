package com.reedelk.plugin.component.type.flowreference;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindJoiningScope;
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.discovery.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class FlowReferenceComponentDiscovery extends AbstractDiscoveryStrategy {

    public FlowReferenceComponentDiscovery(Module module, PlatformComponentServiceImpl componentService) {
        super(module, componentService);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        // List
        CountDownLatch latch = new CountDownLatch(1);
        FlowGraph[] deserialize = new FlowGraph[1];
        ReadAction.nonBlocking(new Runnable() {
            @Override
            public void run() {

                SubflowMetadata subflowMetadata = SubflowService.getInstance(module)
                        .listSubflows()
                        .stream()
                        .filter(subflowMetadata1 -> {
                            String flowReference = predecessor.componentData().get(JsonParser.FlowReference.ref());
                            return subflowMetadata1.getId().equals(flowReference);
                        }).findFirst().orElse(null);

                String filePath = subflowMetadata.getFileURL();
                // TODO: File path might be null!!
                VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(filePath);
                Document document = FileDocumentManager.getInstance().getDocument(fileByUrl);

                try {
                    deserialize[0] = SubFlowDeserializer.deserialize(module, document.getText(), new FlowGraphProvider());
                } catch (DeserializationError deserializationError) {
                    deserializationError.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }).inSmartMode(module.getProject())
                .submit(AppExecutorUtil.getAppExecutorService());

        try {
            latch.await();

            MyContext newContext = new MyContext(deserialize[0], predecessor);
            return DiscoveryStrategyFactory.get(newContext, module, componentService, predecessor);

        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    static class MyContext extends ContainerContext {

        private final FlowGraph subflowGraph;

        public MyContext(@NotNull FlowGraph subflowGraph, @NotNull GraphNode flowReference) {
            super(null, flowReference, "does not matter");
            this.subflowGraph = subflowGraph;
        }

        @Override
        public GraphNode node() {
            return super.node();
        }

        @Override
        public Optional<ScopedGraphNode> joiningScope() {
            return FindJoiningScope.of(subflowGraph, node());
        }

        @Override
        public List<GraphNode> successors(GraphNode node) {
            return subflowGraph.successors(node);
        }

        @Override
        public GraphNode predecessor() {
            List<GraphNode> predecessors = subflowGraph.endNodes();
            return predecessors.stream().findFirst().orElse(null);
        }

        @Override
        public List<GraphNode> endNodes() {
            return subflowGraph.endNodes();
        }

        @Override
        public List<GraphNode> predecessors() {
            return subflowGraph.endNodes();
        }

        @Override
        public List<GraphNode> predecessors(GraphNode node) {
            if (node == node()) {
                return subflowGraph.endNodes();
            } else {
                return subflowGraph.predecessors(node);
            }
        }

        @Override
        public GraphNode predecessor(GraphNode graphNode) {
            List<GraphNode> predecessors = predecessors(graphNode);
            return predecessors.stream().findFirst().orElse(null);
        }
    }
}
