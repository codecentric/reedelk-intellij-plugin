package com.reedelk.plugin.component.type.flowreference;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.deserializer.DeserializationError;
import com.reedelk.plugin.graph.deserializer.SubFlowDeserializer;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.subflow.SubflowMetadata;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FlowReferenceComponentDiscovery extends AbstractDiscoveryStrategy {

    public FlowReferenceComponentDiscovery(Module module, PlatformComponentServiceImpl componentService) {
        super(module, componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        // List
        SubflowService.getInstance(module).listSubflows().forEach(new Consumer<SubflowMetadata>() {
            @Override
            public void accept(SubflowMetadata subflowMetadata) {
                String filePath = subflowMetadata.getFileURL();
                // TODO: File path might be null!!
                VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(filePath);
                Document document = FileDocumentManager.getInstance().getDocument(fileByUrl);
                try {
                    FlowGraph deserialize = SubFlowDeserializer.deserialize(module, document.getText(), new FlowGraphProvider());
                    List<GraphNode> graphNodes = deserialize.endNodes();


                } catch (DeserializationError deserializationError) {
                    deserializationError.printStackTrace();
                }

            }
        });
        return Optional.empty();
    }
}
