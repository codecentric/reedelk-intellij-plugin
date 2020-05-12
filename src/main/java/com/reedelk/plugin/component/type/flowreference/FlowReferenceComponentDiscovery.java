package com.reedelk.plugin.component.type.flowreference;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindJoiningScope;
import com.reedelk.plugin.graph.utils.FindScopes;
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;
import com.reedelk.runtime.commons.JsonParser;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

public class FlowReferenceComponentDiscovery extends AbstractDiscoveryStrategy {

    public FlowReferenceComponentDiscovery(Module module, PlatformComponentService componentService, TrieMapWrapper typeAndAndTries) {
        super(module, componentService, typeAndAndTries);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom) {
        // List
        CountDownLatch latch = new CountDownLatch(1);
        FlowGraph[] deserialize = new FlowGraph[1];
        ReadAction.nonBlocking(new Runnable() {
            @Override
            public void run() {

                try {
                SubflowMetadata subflowMetadata = SubflowService.getInstance(module)
                        .listSubflows()
                        .stream()
                        .filter(subflowMetadata1 -> {
                            String flowReference = nodeWeWantOutputFrom.componentData().get(JsonParser.FlowReference.ref());
                            return subflowMetadata1.getId().equals(flowReference);
                        }).findFirst().orElse(null);

                // TODO: Subflow metadata might be null
                if (subflowMetadata == null) return;

                String filePath = subflowMetadata.getFileURL();
                // TODO: File path might be null!!
                VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(filePath);
                Document document = FileDocumentManager.getInstance().getDocument(fileByUrl);


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

            if (deserialize[0] != null) {
                SubflowGraphAwareContext newContext = new SubflowGraphAwareContext(deserialize[0], nodeWeWantOutputFrom);
                return DiscoveryStrategyFactory.get(module, componentService, typeAndAndTries, newContext, nodeWeWantOutputFrom);
            } else {
                return Optional.empty();
            }

        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    static class SubflowGraphAwareContext extends ComponentContext {

        public SubflowGraphAwareContext(FlowGraph subflowGraph, GraphNode nodeWeWantOutputFrom) {
            super(subflowGraph, nodeWeWantOutputFrom, null);
        }

        @Override
        public Optional<ScopedGraphNode> joiningScopeOf(GraphNode target) {
            List<GraphNode> graphNodes = graph.endNodes();
            if (graphNodes.size() > 1) {
                Stack<ScopedGraphNode> of = FindScopes.of(graph, graphNodes.get(1));
                ScopedGraphNode last = of.pop();
                while(graphNodes.isEmpty()) {
                    last = of.pop();
                }
                return Optional.of(last);
            } else {
                return FindJoiningScope.of(graph, target);
            }
        }

        @Override
        public List<GraphNode> predecessors(GraphNode target) {
            if (target == node()) {
                return graph.endNodes();
            } else {
                return graph.predecessors(target);
            }
        }
    }
}
