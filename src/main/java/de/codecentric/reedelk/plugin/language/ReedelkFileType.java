package de.codecentric.reedelk.plugin.language;

import de.codecentric.reedelk.plugin.commons.Icons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReedelkFileType extends LanguageFileType {

    public static final ReedelkFileType INSTANCE = new ReedelkFileType();

    protected ReedelkFileType() {
        super(ReedelkLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Reedelk";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getDescription() {
        return "Reedelk file type";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "reedelk";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FileTypeSubFlow;
    }
}
