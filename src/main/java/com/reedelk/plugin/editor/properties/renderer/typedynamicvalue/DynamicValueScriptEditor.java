package com.reedelk.plugin.editor.properties.renderer.typedynamicvalue;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.commons.Defaults.DEFAULT_DYNAMIC_VALUE_SCRIPT_VIRTUAL_FILE_NAME;
import static com.reedelk.plugin.commons.Fonts.ScriptEditor.SCRIPT_EDITOR_FONT_SIZE;
import static java.util.Collections.singletonList;

public class DynamicValueScriptEditor extends ScriptEditor {

    public DynamicValueScriptEditor(Module module, ContainerContext context) {
        super(module, emptyDocument(), context);
    }

    private static Document emptyDocument() {
        VirtualFile myVirtualFile = new LightVirtualFile(DEFAULT_DYNAMIC_VALUE_SCRIPT_VIRTUAL_FILE_NAME, PlainTextFileType.INSTANCE, StringUtils.EMPTY);
        return FileDocumentManager.getInstance().getDocument(myVirtualFile);
    }

    @Override
    protected void configure(EditorEx editor) {
        editor.setOneLineMode(true);
        editor.setBorder(Borders.empty());
        editor.getScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        EditorColorsScheme colorsScheme = editor.getColorsScheme();
        colorsScheme.setEditorFontSize(SCRIPT_EDITOR_FONT_SIZE);

        EditorSettings settings = editor.getSettings();
        settings.setRightMargin(0);
        settings.setAdditionalLinesCount(0);
        settings.setAdditionalColumnsCount(0);
        settings.setSoftMargins(singletonList(0));
        settings.setBlockCursor(false);
        settings.setCaretRowShown(false);
        settings.setGutterIconsShown(false);
        settings.setLineNumbersShown(false);
        settings.setShowIntentionBulb(false);
        settings.setLineMarkerAreaShown(false);
        settings.setAdditionalPageAtBottom(false);
        settings.setLeadingWhitespaceShown(false);
        settings.setAllowSingleLogicalLineFolding(false);
    }
}
