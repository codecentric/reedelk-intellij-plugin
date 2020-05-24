package com.reedelk.plugin.component.type.flowreference.discovery;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
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
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SubflowDeserializer {

    private static final Logger LOG = Logger.getInstance(SubflowDeserializer.class);

    private final Module module;
    private final String flowReferenceId;

    public SubflowDeserializer(Module module, String flowReferenceId) {
        this.module = module;
        this.flowReferenceId = flowReferenceId;
    }

    public Optional<FlowGraph> deserialize() {
        FlowGraph[] deserialize = new FlowGraph[1];
        CountDownLatch latch = new CountDownLatch(1);

        ReadAction.nonBlocking(() -> {

            try {
                SubflowService.getInstance(module)
                        .listSubflows()
                        .stream()
                        .filter(subflowMetadata -> subflowMetadata.getId().equals(flowReferenceId)).findFirst()
                        .map(SubflowMetadata::getFileURL).flatMap(fileURL -> {
                            VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(fileURL);
                            return Optional.ofNullable(virtualFile);
                        }).flatMap(virtualFile -> {
                            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                            return Optional.ofNullable(document);
                        }).ifPresent(document -> {
                            try {
                                FlowGraphProvider provider = FlowGraphProvider.get();
                                deserialize[0] = SubFlowDeserializer.deserialize(module, document.getText(), provider);
                            } catch (DeserializationError error) {
                                LOG.warn(error);
                            }
                        });
            } finally {
                latch.countDown();
            }

        }).inSmartMode(module.getProject()).submit(AppExecutorUtil.getAppExecutorService());

        try {
            // If we can't deserialize in 3 seconds, the stop.
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            // nothing to do.
        }

        return deserialize[0] != null ?
                Optional.of(deserialize[0]) :
                Optional.empty();
    }
}
