package com.esb.plugin.service.module.impl;

import com.esb.internal.commons.FileExtension;
import com.esb.internal.commons.JsonParser;
import com.esb.plugin.service.module.SubflowService;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                JSONObject subFlowDefinition = new JSONObject(json);
                String id = JsonParser.Subflow.id(subFlowDefinition);
                String title = JsonParser.Subflow.title(subFlowDefinition);
                return Optional.of(new SubflowMetadata(id, title));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
