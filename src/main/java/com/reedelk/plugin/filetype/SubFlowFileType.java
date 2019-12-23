package com.reedelk.plugin.filetype;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SubFlowFileType extends LanguageFileType {

    public static final SubFlowFileType INSTANCE = new SubFlowFileType();

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
        return FileExtension.SUBFLOW.value();
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FileTypeSubFlow;
    }
}
