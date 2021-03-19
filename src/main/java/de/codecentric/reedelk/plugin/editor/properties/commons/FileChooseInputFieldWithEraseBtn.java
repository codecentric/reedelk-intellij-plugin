package de.codecentric.reedelk.plugin.editor.properties.commons;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import com.intellij.openapi.project.Project;
import de.codecentric.reedelk.plugin.commons.Icons;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.EMPTY;

public class FileChooseInputFieldWithEraseBtn extends DisposablePanel {

    private static final int INPUT_FILE_FIELD_COLUMNS = 38;

    private final PropertyAccessor propertyAccessor;
    private final FileChooseInputField fileChooseInputField;

    public FileChooseInputFieldWithEraseBtn(@NotNull Project project,
                                            @NotNull String title,
                                            @Nullable String hintText,
                                            @Nullable String rootDirectory,
                                            @NotNull PropertyAccessor propertyAccessor,
                                            int columns) {
        super(new BorderLayout());
        this.propertyAccessor = propertyAccessor;
        this.fileChooseInputField =
                new FileChooseInputField(project, title, hintText, rootDirectory, propertyAccessor, columns);

        // Erase Button clear the ChooseFileInputField value.
        ClickableLabel eraseButton = new ClickableLabel(Icons.Misc.Erase, this::erase);
        eraseButton.setBorder(emptyLeft(5));

        add(fileChooseInputField, BorderLayout.WEST);
        add(ContainerFactory.pushLeft(eraseButton), BorderLayout.CENTER);
    }

    public FileChooseInputFieldWithEraseBtn(@NotNull Project project,
                                            @NotNull String title,
                                            @Nullable String hintText,
                                            @Nullable String rootDirectory,
                                            @NotNull PropertyAccessor propertyAccessor) {
        this(project, title, hintText, rootDirectory, propertyAccessor, INPUT_FILE_FIELD_COLUMNS);
    }

    public void erase() {
        propertyAccessor.set(null);
        fileChooseInputField.setText(null);
    }


    public static class PropertyAccessorInMemory implements PropertyAccessor {

        private Object value = EMPTY;

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
