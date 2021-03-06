package de.codecentric.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.commons.FormBuilder;
import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.commons.StringInputField;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import de.codecentric.reedelk.plugin.commons.ScriptFileNameValidator;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class DialogAddScript extends DialogWrapper {

    private String scriptFileNameWithPathToAdd;

    DialogAddScript(@Nullable Project project) {
        super(project, false);
        setTitle(message("script.dialog.add.title"));
        setResizable(false);
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, message("script.dialog.add.btn.add"));
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new GridBagLayout());

        InputField<String> field = new StringInputField(StringUtils.EMPTY);
        field.addListener(value -> scriptFileNameWithPathToAdd = (String) value);
        FormBuilder.get()
                .addLabel(message("script.dialog.add.label.file.name"), panel)
                .addLastField(field, panel);

        panel.setPreferredSize(new Dimension(350, 0));
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (!ScriptFileNameValidator.validate(scriptFileNameWithPathToAdd)) {
            return new ValidationInfo(message("script.file.name.validation.error"));
        }
        return super.doValidate();
    }

    String getScriptFileNameIncludingPathToAdd() {
        return scriptFileNameWithPathToAdd;
    }
}
