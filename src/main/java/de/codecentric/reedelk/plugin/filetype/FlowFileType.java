package de.codecentric.reedelk.plugin.filetype;

import de.codecentric.reedelk.plugin.commons.Icons;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import com.intellij.json.JsonLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import de.codecentric.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FlowFileType extends LanguageFileType {

    public FlowFileType() {
        super(JsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return ReedelkBundle.message("file.type.flow.name");
    }

    @NotNull
    @Override
    public String getDescription() {
        return ReedelkBundle.message("file.type.flow.description");
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return FileExtension.FLOW.value();
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FileTypeFlow;
    }
}
