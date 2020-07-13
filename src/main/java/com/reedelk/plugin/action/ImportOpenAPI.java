package com.reedelk.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.pom.Navigatable;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.ChooseFileInputField;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ImportOpenAPI extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        // Enable / Disable action (this is always enabled)
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project currentProject = event.getProject();
        StringBuffer dlgMsg = new StringBuffer(event.getPresentation().getText() + " Selected!");
        String dlgTitle = event.getPresentation().getDescription();
        // If an element is selected in the editor, add info about it.
        Navigatable nav = event.getData(CommonDataKeys.NAVIGATABLE);
        if (nav != null) {
            dlgMsg.append(String.format("\nSelected Element: %s", nav.toString()));
        }
        //Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon());
        SelectOpenAPIFileDialog dialogAddScript = new SelectOpenAPIFileDialog(currentProject);
        boolean result = dialogAddScript.showAndGet();
        if (result) {
            String openAPIFilePath = dialogAddScript.getOpenAPIFilePath();
            System.out.println(openAPIFilePath);
        }

    }

    static class SelectOpenAPIFileDialog extends DialogWrapper {

        private final Project project;
        private InMemoryPropertyAccessor inMemoryPropertyAccessor = new InMemoryPropertyAccessor();

        protected SelectOpenAPIFileDialog(@Nullable Project project) {
            super(project);
            this.project = project;
            setResizable(false);
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JBLabel("Select OpenAPI"), BorderLayout.NORTH);

            String title = "Select Open API specification";
            String hintText = "Select Open API specification";

            ChooseFileInputField chooseFileInputField =
                    new ChooseFileInputField(project,
                    title, hintText, StringUtils.EMPTY, inMemoryPropertyAccessor);
            panel.add(chooseFileInputField, BorderLayout.CENTER);
            return panel;
        }

        String getOpenAPIFilePath() {
            return inMemoryPropertyAccessor.get();
        }
    }

    static class InMemoryPropertyAccessor implements PropertyAccessor {

        private Object value = StringUtils.EMPTY;

        @Override
        public FlowSnapshot getSnapshot() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> void set(T object) {
            this.value = object;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get() {
            return (T) this.value;
        }

        @Override
        public String getProperty() {
            throw new UnsupportedOperationException();
        }
    }
}
