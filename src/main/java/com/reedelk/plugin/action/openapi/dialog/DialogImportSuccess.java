package com.reedelk.plugin.action.openapi.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.lang.String.format;

public class DialogImportSuccess extends DialogWithMessage {

    public DialogImportSuccess(@Nullable Project project, String message) {
        super(project, formatMessage(message), AllIcons.General.InformationDialog);
    }

    @Override
    public String getTitle() {
        return message("openapi.importer.dialog.success.title");
    }

    private static String formatMessage(String message) {
        String messageFormat = "<html><p>%s</p><br><p>%s</p></html>";
        return format(messageFormat, message("openapi.importer.dialog.success.message"), message);
    }
}
