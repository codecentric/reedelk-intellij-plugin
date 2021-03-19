package de.codecentric.reedelk.plugin.commons;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

public class ScriptFileUtils {

    private ScriptFileUtils() {
    }

    public static Document createInMemoryDocument(String componentPropertyPath) {
        return createInMemoryDocument(componentPropertyPath, StringUtils.EMPTY);
    }

    public static Document createInMemoryDocument(String componentPropertyPath, String content) {
        VirtualFile myVirtualFile =
                new LightVirtualFile(String.format(DefaultConstants.SCRIPT_TMP_FILE_NAME, componentPropertyPath), DefaultConstants.SCRIPT_FILE_TYPE, content);
        return FileDocumentManager.getInstance().getDocument(myVirtualFile);
    }
}
