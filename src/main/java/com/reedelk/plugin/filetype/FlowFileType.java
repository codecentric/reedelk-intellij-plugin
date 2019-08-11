package com.reedelk.plugin.filetype;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reedelk.plugin.commons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FlowFileType extends LanguageFileType {

    public static final FlowFileType INSTANCE = new FlowFileType();
    public static final String DEFAULT_EXTENSION = "flow";


    public FlowFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Flow";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ESB Flow";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FileTypeFlow;
    }
}
