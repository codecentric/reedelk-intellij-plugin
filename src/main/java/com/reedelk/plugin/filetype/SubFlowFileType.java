package com.reedelk.plugin.filetype;

import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.message.ReedelkBundle;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SubFlowFileType extends LanguageFileType {

    public SubFlowFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return ReedelkBundle.message("file.type.subflow.name");
    }

    @NotNull
    @Override
    public String getDescription() {
        return ReedelkBundle.message("file.type.subflow.description");
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
