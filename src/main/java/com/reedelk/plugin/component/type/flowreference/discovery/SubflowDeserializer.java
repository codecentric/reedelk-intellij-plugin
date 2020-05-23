package com.reedelk.plugin.component.type.flowreference.discovery;

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
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SubflowDeserializer {

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
                SubflowMetadata flowReferenceSubflowMetadata = SubflowService.getInstance(module)
                        .listSubflows()
                        .stream()
                        .filter(subflowMetadata -> subflowMetadata.getId().equals(flowReferenceId)).findFirst()
                        .orElse(null);

                // TODO: Subflow metadata might be null
                if (flowReferenceSubflowMetadata == null) return;

                String filePath = flowReferenceSubflowMetadata.getFileURL();
                // TODO: File path might be null!!
                VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(filePath);
                Document document = FileDocumentManager.getInstance().getDocument(fileByUrl);
                deserialize[0] = SubFlowDeserializer.deserialize(module, document.getText(), new FlowGraphProvider());
            } catch (DeserializationError deserializationError) {
                deserializationError.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).inSmartMode(module.getProject())
                .submit(AppExecutorUtil.getAppExecutorService());

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
