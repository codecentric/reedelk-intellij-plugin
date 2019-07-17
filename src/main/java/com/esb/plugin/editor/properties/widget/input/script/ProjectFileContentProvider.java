package com.esb.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

public class ProjectFileContentProvider {

    public String getContent(String fileUrl) {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
        if (file != null) {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                return document.getText();
            }
        }

        throw new IllegalStateException("File not found");
    }

    public String getParentFolder(String fileUrl) {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
        if (file != null) {
            return file.getParent().getUrl();
        }
        throw new IllegalStateException();
    }
}
