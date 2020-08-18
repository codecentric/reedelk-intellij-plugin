package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.HintPainter;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.*;

public class ChooseFileInputField extends TextFieldWithBrowseButton {

    private static final int INPUT_FILE_FIELD_COLUMNS = 38;

    public ChooseFileInputField(@NotNull Project project,
                                @NotNull String title,
                                @Nullable String hintText,
                                @Nullable String rootDirectory,
                                @NotNull PropertyAccessor propertyAccessor) {
        // to prevent field to be infinitely re-sized in grid-box layouts
        super(new InputFieldWithHint(hintText, INPUT_FILE_FIELD_COLUMNS), null);

        String initText = propertyAccessor.get();

        setEditable(false);
        setText(initText);

        DefaultFileChooserDescriptor fileChooserDescriptor = new DefaultFileChooserDescriptor(rootDirectory, title);

        TextComponentAccessor<JTextField> textComponentAccessor = isBlank(rootDirectory) ?
                new FileSystemTextAccessor(propertyAccessor) :
                new ProjectFileAwareTextAccessor(rootDirectory, propertyAccessor);

        BrowseFolderActionListener<JTextField> actionListener =
                new BrowseFolderActionListener<>(title, initText, this, project, fileChooserDescriptor, textComponentAccessor);

        addActionListener(actionListener);
        installPathCompletion(fileChooserDescriptor);
    }


    /**
     * The file must belong to the root directory.
     */
    static class DefaultFileChooserDescriptor extends FileChooserDescriptor {

        private final String rootDirectory;

        public DefaultFileChooserDescriptor(String rootDirectory, String chooseFileDialogTitle) {
            super(true, false, true, true, false, false);
            this.rootDirectory = rootDirectory;
            this.setTitle(chooseFileDialogTitle);
        }

        @Override
        public void validateSelectedFiles(@NotNull VirtualFile[] files) throws Exception {
            if (!files[0].getPresentableUrl().startsWith(rootDirectory)) {
                // IMPORTANT: The file needs to belong to the current module
                throw new IllegalArgumentException(message("properties.type.resource.choose.file.dialog.error.not.resource"));
            }
        }
    }

    /**
     * Input Text Field With Hint.
     */
    static class InputFieldWithHint extends ExtendableTextField {

        private final String hintText;

        InputFieldWithHint(String hintText, int columns) {
            super(columns);
            this.hintText = hintText;
            setEditable(false);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            HintPainter.from(g, this, hintText, Colors.PROPERTIES_FILE_INPUT_FIELD_HINT);
        }
    }

    static class FileSystemTextAccessor implements TextComponentAccessor<JTextField> {

        private final PropertyAccessor propertyAccessor;

        FileSystemTextAccessor(PropertyAccessor propertyAccessor) {
            this.propertyAccessor = propertyAccessor;
        }

        @Override
        public String getText(JTextField component) {
            return propertyAccessor.get();
        }

        @Override
        public void setText(JTextField component, @NotNull String text) {
            component.setText(text);
            propertyAccessor.set(text);
        }
    }

    /**
     * Property accessor with root directory. This text accessor removes the root directory
     * from the text to be set in the input field. This is used by the components where
     * a 'browse file' is used to select a file within the project.
     * IMPORTANT: Note that projects files are separated by '/' path and not from the OS dependent path separator.
     */
    static class ProjectFileAwareTextAccessor implements TextComponentAccessor<JTextField> {

        private final String rootDirectory;
        private final PropertyAccessor propertyAccessor;

        ProjectFileAwareTextAccessor(String rootDirectory, PropertyAccessor propertyAccessor) {
            this.rootDirectory = rootDirectory;
            this.propertyAccessor = propertyAccessor;
        }

        @Override
        public String getText(JTextField component) {
            String filePath = propertyAccessor.get();
            return filePath == null ?
                    rootDirectory + "/" :
                    rootDirectory + "/" + filePath;
        }

        @Override
        public void setText(@NotNull JTextField component, @NotNull String text) {
            if (isNotBlank(text)) {
                // We must remove the root from the selected path. This is needed
                // in order to set in the property accessor only the subpath
                // we are interested in. For instance we might only write in the
                // JSON file the subdirectory starting from {PROJECT_HOME}/src/main/resources
                // for the Resource Read Component.
                String fileRelativePath = EMPTY;
                if (text.length() > rootDirectory.length()) {
                    fileRelativePath = text.substring(rootDirectory.length() + 1);
                }

                String normalizedRelativePath = normalizeProjectFilePath(fileRelativePath);
                component.setText(normalizedRelativePath);
                propertyAccessor.set(normalizedRelativePath);
            } else {
                propertyAccessor.set(EMPTY);
            }
        }

        // Project files are referenced using '/' front slash because they belong to the .jar package and
        // they are NOT OS dependent.
        private static String normalizeProjectFilePath(String value) {
            return value.replaceAll("\\\\", "\\/");
        }
    }
}
