package com.reedelk.plugin.action.openapi.dialog;

import com.intellij.openapi.project.Project;
import com.reedelk.plugin.editor.properties.commons.DialogWithMessage;
import org.jetbrains.annotations.Nullable;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.lang.String.format;

public class DialogImportError extends DialogWithMessage {

    public DialogImportError(@Nullable Project project, String cause) {
        super(project, formatMessage(cause), DialogType.ERROR);
    }

    @Override
    public String getTitle() {
        return message("openapi.importer.dialog.error.title");
    }

    private static String formatMessage(String message) {
        String messageFormat = "<html><p>%s</p><br><p>%s</p></html>";
        return format(messageFormat, message("openapi.importer.dialog.error.message"), message);
    }
}
