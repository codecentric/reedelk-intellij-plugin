package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScriptInputField extends JPanel implements ActionListener, DocumentListener {

    private InputChangeListener<String> listener;

    private JLabel openEditorBtn;
    private JavascriptEditor editor;


    private final Module module;
    private String value;

    public ScriptInputField(@NotNull Module module) {
        this.module = module;
        this.editor = new JavascriptEditor(module.getProject());
        this.editor.addDocumentListener(this);

        this.openEditorBtn = new JLabel("Open Editor");
        this.openEditorBtn.setIcon(Icons.Script.Edit);
        this.openEditorBtn.setDisabledIcon(Icons.Script.Edit);
        this.openEditorBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (value == null) value = "";
                EditScriptDialog editScriptDialog = new EditScriptDialog(module, editor.getValue());
                if (editScriptDialog.showAndGet()) {
                    value = editScriptDialog.getValue();
                    listener.onChange(value);
                    editor.setValue(value);
                }
            }

        });


        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(openEditorBtn, BorderLayout.NORTH);
        wrapper.setBorder(BorderFactory.createEmptyBorder(3, 0, 10, 0));


        setLayout(new BorderLayout());
        add(wrapper, BorderLayout.NORTH);
        add(editor, BorderLayout.CENTER);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        if (listener != null) {
            listener.onChange(event.getDocument().getText());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditScriptDialog editScriptDialog = new EditScriptDialog(module, value);
        if (editScriptDialog.showAndGet()) {
            this.value = editScriptDialog.getValue();
            listener.onChange(this.value);
        }
    }

    public void addListener(InputChangeListener<String> listener) {
        this.listener = listener;
    }

    public void setValue(Object o) {
        this.value = (String) o;
        this.editor.setValue(this.value);
    }
}
