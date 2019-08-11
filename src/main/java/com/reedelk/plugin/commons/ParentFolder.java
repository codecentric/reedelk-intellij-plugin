package com.reedelk.plugin.commons;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

public class ParentFolder {

    public static String of(String fileUrl) {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
        if (file != null) {
            return file.getParent().getUrl();
        }
        throw new IllegalStateException();
    }
}
