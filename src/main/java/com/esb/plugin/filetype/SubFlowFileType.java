package com.esb.plugin.filetype;

import com.esb.plugin.commons.Icons;
import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SubFlowFileType extends LanguageFileType {

    public static final SubFlowFileType INSTANCE = new SubFlowFileType();
    public static final String DEFAULT_EXTENSION = "subflow";

    public SubFlowFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Subflow";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ESB Subflow";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FileTypeSubFlow;
    }
}
