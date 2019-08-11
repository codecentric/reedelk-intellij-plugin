package com.reedelk.plugin.commons;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.util.Optional;

public class ProjectFileContentProvider {

    private ProjectFileContentProvider() {
    }

    public static Optional<String> of(String fileUrl) {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
        if (file != null) {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                return Optional.of(document.getText());
            }
        }
        return Optional.empty();
    }
}
