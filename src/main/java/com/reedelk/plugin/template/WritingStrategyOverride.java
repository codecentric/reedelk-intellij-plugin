package com.reedelk.plugin.template;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Optional;

/**
 * Default strategy overriding the content of existing file if it exists already,
 * otherwise a new file will be created with the template content.
 */
public class WritingStrategyOverride implements WritingStrategy {

    @Override
    public Optional<VirtualFile> write(String templateText, VirtualFile destinationDir, String fileName) throws IOException {
        VirtualFile file = destinationDir.findOrCreateChildData(this, fileName);
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {
            // File already cached, therefore we must update the cached file and *not*
            // the one in the file system.
            document.setText(templateText);
        } else {
            // File not cached, we can save the text on the file system.
            VfsUtil.saveText(file, templateText);
        }
        return Optional.of(file);
    }
}
