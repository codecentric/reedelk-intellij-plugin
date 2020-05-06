package com.reedelk.plugin.editor.properties.renderer.typedynamicvalue;

import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.ScriptFileUtils;
import com.reedelk.plugin.editor.properties.commons.ScriptEditor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;

import javax.swing.*;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.commons.Fonts.ScriptEditor.SCRIPT_EDITOR_FONT_SIZE;
import static java.util.Collections.singletonList;

public class DynamicValueScriptEditor extends ScriptEditor {

    public DynamicValueScriptEditor(Module module,
                                    String scriptPropertyPath,
                                    ContainerContext context) {
        super(module, ScriptFileUtils.createEmptyInMemoryDocument(), scriptPropertyPath, context);
    }

    @Override
    protected void configure(EditorEx editor) {
        editor.setOneLineMode(true);
        editor.setBorder(Borders.empty());
        editor.getScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        editor.getScrollPane().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

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
