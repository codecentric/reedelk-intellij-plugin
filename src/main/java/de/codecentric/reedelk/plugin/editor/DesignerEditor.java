package de.codecentric.reedelk.plugin.editor;

import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.PossiblyDumbAware;

public interface DesignerEditor extends FileEditor, PossiblyDumbAware, DocumentListener {
}
