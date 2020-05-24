package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.DebugControls;
import com.reedelk.plugin.commons.DefaultConstants;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.runtime.api.commons.ScriptUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.openapi.command.WriteCommandAction.writeCommandAction;
import static com.reedelk.plugin.commons.UserData.*;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;

public class ScriptEditor extends DisposablePanel implements DocumentListener {

    private static final Logger LOG = Logger.getInstance(ScriptEditor.class);

    private final String scriptPropertyPath;
    private final transient Module module;
    private final transient EditorEx editor;
    private final transient Document document;
    private transient ScriptEditorChangeListener listener;

    public ScriptEditor(@NotNull Module module,
                        @NotNull Document document,
                        @NotNull String scriptPropertyPath,
                        @NotNull ContainerContext context) {
        this.module = module;
        this.document = document;
        this.scriptPropertyPath = scriptPropertyPath;

        if (DebugControls.Script.SCRIPT_EDITOR_LIFECYCLE) {
            LOG.info("SCRIPT_EDITOR_CREATED (" + scriptPropertyPath + ")");
        }

        ComponentContext componentContext = context.componentContext();

        this.editor = (EditorEx) EditorFactory.getInstance()
                .createEditor(document, module.getProject(), DefaultConstants.SCRIPT_FILE_TYPE, false);
        this.editor.putUserData(MODULE_NAME, module.getName());
        this.editor.putUserData(COMPONENT_CONTEXT, componentContext);
        this.editor.putUserData(COMPONENT_PROPERTY_PATH, scriptPropertyPath);
        this.configure(editor);

        document.addDocumentListener(this);
        setLayout(new BorderLayout());
        add(editor.getComponent(), CENTER);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        if (listener != null) {
            // Notify when script mode changed
            String script = ScriptUtils.asScript(event.getDocument().getText());
            listener.onChange(script);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (editor != null && !editor.isDisposed()) {
            this.editor.putUserData(MODULE_NAME, null);
            this.editor.putUserData(COMPONENT_CONTEXT, null);
            this.editor.putUserData(COMPONENT_PROPERTY_PATH, null);
            EditorFactory.getInstance().releaseEditor(editor);
            if (DebugControls.Script.SCRIPT_EDITOR_LIFECYCLE) {
                LOG.info("SCRIPT_EDITOR_RELEASED (" + scriptPropertyPath + ")");
            }
        }
    }

    @Override
    public void setBackground(Color color) {
        if (this.editor != null) {
            this.editor.setBackgroundColor(color);
        }
    }

    public String getValue() {
        return document.getText();
    }

    public void setValue(@NotNull String value) {
        try {
            String script = ScriptUtils.unwrap(value);
            writeCommandAction(module.getProject()).run(() -> document.setText(script));
        } catch (Exception exception) {
            LOG.error(message("script.error.write", value, exception.getMessage(), exception));
        }
    }

    public void setListener(ScriptEditorChangeListener listener) {
        this.listener = listener;
    }

    protected void configure(EditorEx editor) {
        // optionally add extra configuration to the editor.
    }
}
