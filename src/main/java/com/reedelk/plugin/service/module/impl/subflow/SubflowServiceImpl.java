package com.reedelk.plugin.service.module.impl.subflow;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.service.module.SubflowService;
import com.reedelk.runtime.commons.FileExtension;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.commons.JsonParser.Subflow;

public class SubflowServiceImpl implements SubflowService {

    private final Module module;

    public SubflowServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public List<SubflowMetadata> listSubflows() {
        List<SubflowMetadata> subflows = new ArrayList<>();
        ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
            if (FileExtension.SUBFLOW.value().equals(fileOrDir.getExtension())) {
                getMetadataFrom(fileOrDir).ifPresent(subflows::add);
            }
            return true;
        });
        return subflows;
    }

    private Optional<SubflowMetadata> getMetadataFrom(VirtualFile virtualFile) {
        try {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                String json = document.getText();
                SubflowMetadata metadata = createMetadata(json, virtualFile.getUrl());
                return Optional.of(metadata);
            }
        } catch (Exception e) {
            // nothing to do.
        }
        return Optional.empty();
    }

    private SubflowMetadata createMetadata(String json, String fileURL) {
        JSONObject subFlowDefinition = new JSONObject(json);
        String id = Subflow.id(subFlowDefinition);
        String title = Subflow.title(subFlowDefinition);
        return new SubflowMetadata(id, title, fileURL);
    }
}
