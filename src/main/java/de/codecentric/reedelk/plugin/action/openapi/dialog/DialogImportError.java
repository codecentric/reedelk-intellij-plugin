package de.codecentric.reedelk.plugin.action.openapi.dialog;

import com.intellij.openapi.project.Project;
import de.codecentric.reedelk.plugin.editor.properties.commons.DialogWithMessage;
import org.jetbrains.annotations.Nullable;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class DialogImportError extends DialogWithMessage {

    public DialogImportError(@Nullable Project project, String cause) {
        super(project, formatMessage(cause), DialogType.ERROR);
    }

    @Override
    public String getTitle() {
        return message("openapi.importer.dialog.error.title");
    }

    private static String formatMessage(String message) {
        return message("openapi.importer.dialog.error.message", message);
    }
}
