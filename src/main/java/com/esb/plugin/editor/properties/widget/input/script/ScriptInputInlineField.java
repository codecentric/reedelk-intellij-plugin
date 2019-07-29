package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScriptInputInlineField extends DisposablePanel {

    private JTextField scriptTextField;
    private JLabel editScriptIcon;

    public ScriptInputInlineField() {
        setLayout(new BorderLayout());
        scriptTextField = new JTextField();
        add(scriptTextField, BorderLayout.CENTER);

        editScriptIcon = new JLabel();
        editScriptIcon.setBorder(JBUI.Borders.empty(3));
        editScriptIcon.setIcon(Icons.Script.Edit);
        add(editScriptIcon, BorderLayout.EAST);

        editScriptIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JDialog dialog = new JDialog();
                dialog.setVisible(true);
            }
        });
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (scriptTextField != null) {
            scriptTextField.setBackground(bg);
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (scriptTextField != null) {
            scriptTextField.setForeground(fg);
        }
    }

    public void selected() {
        setBackground(new Color(9, 80, 208));
        setForeground(Color.WHITE);
        editScriptIcon.setIcon(Icons.Script.EditSelected);
    }

    public void focused() {
        setBackground(new Color(202, 202, 202));
        setForeground(Color.BLACK);
        editScriptIcon.setIcon(Icons.Script.Edit);
    }

    public void reset() {
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        editScriptIcon.setIcon(Icons.Script.Edit);
    }

    public String getValue() {
        return scriptTextField.getText();
    }

    public void setValue(String text) {
        this.scriptTextField.setText(text);
    }
}
