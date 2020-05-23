package com.reedelk.plugin.component.type.flowreference;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindJoiningScope;
import com.reedelk.plugin.graph.utils.FindScopes;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

public class FlowReferenceComponentDiscovery implements DiscoveryStrategy {

    protected final PlatformModuleService moduleService;
    protected final TypeAndTries typeAndAndTries;
    protected final Module module;

    public FlowReferenceComponentDiscovery(@NotNull Module module,
                                           @NotNull PlatformModuleService moduleService,
                                           @NotNull TypeAndTries typeAndAndTries) {
        this.typeAndAndTries = typeAndAndTries;
        this.moduleService = moduleService;
        this.module = module;
    }

    // TODO: Fixme
    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom, GraphNode successor) {
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
            latch.await(); // TODO: Await with timeout here!

            if (deserialize[0] != null) {
                SubflowGraphAwareContext newContext = new SubflowGraphAwareContext(deserialize[0], nodeWeWantOutputFrom);
                return discover(newContext, nodeWeWantOutputFrom);
            } else {
                return Optional.empty();
            }

        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode) {
        throw new UnsupportedOperationException();
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }

    static class SubflowGraphAwareContext extends ComponentContext {

        public SubflowGraphAwareContext(FlowGraph subflowGraph, GraphNode nodeWeWantOutputFrom) {
            super(subflowGraph, nodeWeWantOutputFrom);
        }

        @Override
        public Optional<ScopedGraphNode> joiningScopeOf(GraphNode target) {
            List<GraphNode> graphNodes = graph.endNodes();
            if (graphNodes.size() > 1) {
                Stack<ScopedGraphNode> of = FindScopes.of(graph, graphNodes.get(1));
                ScopedGraphNode last = of.pop();
                while (graphNodes.isEmpty()) {
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
