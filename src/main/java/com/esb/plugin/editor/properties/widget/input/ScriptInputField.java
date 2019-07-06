package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.editor.properties.widget.input.script.EditScriptDialog;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScriptInputField extends JButton implements ActionListener {

    private InputChangeListener<String> listener;

    private final Module module;
    private String value;

    public ScriptInputField(@NotNull Module module)  {
        this.addActionListener(this);
        this.module =  module;
        setName("Edit Script");
        setText("Edit Script");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditScriptDialog editScriptDialog = new EditScriptDialog(module, value);
        if (editScriptDialog.showAndGet()) {
            this.value = editScriptDialog.getText();
            listener.onChange(this.value);
        }
    }

    public void addListener(InputChangeListener<String> listener) {
        this.listener =  listener;
    }

    public void setValue(Object o) {
        this.value = (String) o;
    }
}
