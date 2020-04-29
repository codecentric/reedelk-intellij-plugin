package com.reedelk.plugin.commons;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.reedelk.runtime.api.commons.StringUtils;

import static com.reedelk.plugin.commons.DefaultConstants.SCRIPT_FILE_TYPE;
import static com.reedelk.plugin.commons.DefaultConstants.SCRIPT_TMP_FILE_NAME;

public class ScriptFileUtils {

    public static Document createEmptyInMemoryDocument() {
        return createInMemoryDocumentWithContent(StringUtils.EMPTY);
    }

    public static Document createInMemoryDocumentWithContent(String content) {
        VirtualFile myVirtualFile = new LightVirtualFile(SCRIPT_TMP_FILE_NAME, SCRIPT_FILE_TYPE, content);
        return FileDocumentManager.getInstance().getDocument(myVirtualFile);
    }
}
