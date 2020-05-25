package com.reedelk.plugin.component.type.flowreference.discovery;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;

import java.util.Optional;

public class SubflowDeserializer {

    private static final Logger LOG = Logger.getInstance(SubflowDeserializer.class);

    private final Module module;
    private final String flowReferenceId;

    public SubflowDeserializer(Module module, String flowReferenceId) {
        this.module = module;
        this.flowReferenceId = flowReferenceId;
    }

    public Optional<FlowGraph> deserialize() {
        try {
            return ReadAction.compute(() -> SubflowService.getInstance(module)
                    .listSubflows()
                    .stream()
                    .filter(subflowMetadata -> subflowMetadata.getId().equals(flowReferenceId)).findFirst()

                    .map(SubflowMetadata::getFileURL).flatMap(fileURL -> {
                        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(fileURL);
                        return Optional.ofNullable(virtualFile);

                    }).flatMap(virtualFile -> {
                        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                        return Optional.ofNullable(document);

                    }).flatMap(document -> {
                        FlowGraphProvider provider = FlowGraphProvider.get();
                        String documentText = document.getText();
                        try {
                            return Optional.ofNullable(SubFlowDeserializer.deserialize(module, documentText, provider));
                        } catch (DeserializationError exception) {
                            LOG.warn(exception);
                            return Optional.empty();
                        }
                    }));

        } catch (Throwable exception) {
            LOG.warn(exception);
            return Optional.empty();
        }
    }
}
