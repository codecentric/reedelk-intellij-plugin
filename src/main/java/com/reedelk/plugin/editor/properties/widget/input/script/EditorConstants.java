package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;

interface EditorConstants {
    FileType JAVASCRIPT_FILE_TYPE = FileTypeManager.getInstance().getFileTypeByExtension("js");
}
