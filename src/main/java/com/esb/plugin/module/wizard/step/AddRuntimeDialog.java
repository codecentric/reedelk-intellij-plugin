package com.esb.plugin.module.wizard.step;

import com.esb.plugin.module.ESBModuleBuilder;
import com.esb.plugin.service.runtime.ESBRuntime;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.esb.plugin.ESBLabel.RUNTIME_HOME;
import static com.intellij.uiDesigner.core.GridConstraints.*;

public class AddRuntimeDialog extends DialogWrapper {

    private TextFieldWithBrowseButton inputWithBrowse;
    private JTextField runtimeNameTextField;
    private JPanel jPanel;


    protected AddRuntimeDialog(@NotNull Component parent, WizardContext wizardContext, ESBModuleBuilder moduleBuilder) {
        super(parent, false);
        setTitle("Add Runtime");

        init();

        buildRuntimeInputField(wizardContext, moduleBuilder);

        DocumentAdapter documentListener = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                doValidateInput();
            }
        };

        runtimeNameTextField.getDocument().addDocumentListener(documentListener);
        inputWithBrowse.getTextField().getDocument().addDocumentListener(documentListener);

        doValidateInput();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jPanel;
    }

    public ESBRuntime getRuntime() {
        ESBRuntime runtime = new ESBRuntime();
        runtime.name = runtimeNameTextField.getText();
        runtime.path = inputWithBrowse.getText();
        return runtime;
    }

    private void buildRuntimeInputField(WizardContext context, ESBModuleBuilder moduleBuilder) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        TextFieldWithBrowseButton inputWithBrowse = new TextFieldWithBrowseButton();
        inputWithBrowse.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, context.getProject()) {
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                String contentEntryPath = moduleBuilder.getContentEntryPath();
                String path = chosenFile.getPath();
                return contentEntryPath == null ? path : path.substring(StringUtil.commonPrefixLength(contentEntryPath, path));
            }
        });

        JLabel label = new JBLabel(RUNTIME_HOME.value());
        label.setLabelFor(inputWithBrowse);
        label.setVerticalAlignment(SwingConstants.TOP);

        jPanel.add(label, CHOOSE_RUNTIME_LABEL_GRID_CONSTRAINTS);
        jPanel.add(inputWithBrowse, CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS);

        this.inputWithBrowse = inputWithBrowse;
    }

    private void doValidateInput() {
        List<String> errors = new ArrayList<>();
        if (StringUtil.isEmptyOrSpaces(runtimeNameTextField.getText())) errors.add("Runtime Name");
        if (StringUtil.isEmptyOrSpaces(inputWithBrowse.getText())) errors.add("Runtime Path");

        if (errors.isEmpty()) {
            setErrorText(null);
            getOKAction().setEnabled(true);
            return;
        }
        String message = "Please specify " + StringUtil.join(errors, ", ");
        setErrorText(message);
        getOKAction().setEnabled(false);
        getRootPane().revalidate();
    }


    private static final GridConstraints CHOOSE_RUNTIME_LABEL_GRID_CONSTRAINTS =
            new GridConstraints(1, 0, 1, 1,
                    ANCHOR_CENTER,
                    FILL_NONE,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    new Dimension(-1, -1),
                    new Dimension(-1, -1),
                    new Dimension(-1, -1));

    private static final GridConstraints CHOOSE_RUNTIME_INPUT_GRID_CONSTRAINTS =
            new GridConstraints(1, 1, 1, 1,
                    ANCHOR_CENTER,
                    FILL_NONE,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK,
                    new Dimension(-1, -1),
                    new Dimension(400, -1),
                    new Dimension(-1, -1));


}
