package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.emptyLeft;

public class ChooseFileInputFieldWithEraseBtn extends DisposablePanel {

    public ChooseFileInputFieldWithEraseBtn(@NotNull Project project,
                                            @NotNull String title,
                                            @Nullable String hintText,
                                            @Nullable String rootDirectory,
                                            @NotNull PropertyAccessor propertyAccessor) {
        super(new BorderLayout());
        ChooseFileInputField chooseFileInputField =
                new ChooseFileInputField(project, title, hintText, rootDirectory, propertyAccessor);

        // Erase Button clear the ChooseFileInputField value.
        ClickableLabel eraseButton = new ClickableLabel(Icons.Misc.Erase, () -> {
            propertyAccessor.set(null);
            chooseFileInputField.setText(null);
        });
        eraseButton.setBorder(emptyLeft(5));

        add(chooseFileInputField, BorderLayout.WEST);
        add(ContainerFactory.pushLeft(eraseButton), BorderLayout.CENTER);
    }
}
