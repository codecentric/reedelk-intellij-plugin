package com.reedelk.plugin.editor.properties.renderer.commons;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.reedelk.runtime.commons.FileExtension;

public interface ScriptEditorConstants {

    FileType JAVASCRIPT_FILE_TYPE = FileTypeManager.getInstance().getFileTypeByExtension(FileExtension.SCRIPT.value());

}
