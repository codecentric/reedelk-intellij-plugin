package com.esb.plugin.filetype;

import com.esb.plugin.utils.ESBIcons;
import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FlowConfigFileType extends LanguageFileType {

    public static final FlowConfigFileType INSTANCE = new FlowConfigFileType();
    public static final String DEFAULT_EXTENSION = "fconfig";


    public FlowConfigFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Flow Config";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ESB Flow Config";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ESBIcons.FileTypeFlowConfig;
    }
}
