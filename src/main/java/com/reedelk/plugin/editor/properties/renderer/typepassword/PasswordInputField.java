package com.reedelk.plugin.editor.properties.renderer.typepassword;

import com.intellij.ui.components.JBPasswordField;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.commons.InputChangeListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import static com.reedelk.plugin.commons.Icons.Misc.HidePassword;
import static com.reedelk.plugin.commons.Icons.Misc.ShowPassword;

public class PasswordInputField extends DisposablePanel implements DocumentListener {

    private transient ValueConverter<?> converter = ValueConverterFactory.forType(String.class);
    private transient InputChangeListener listener;
    private ClickableLabel showPassword;
    private JBPasswordField passwordField;

    private boolean isPasswordHidden = true;

    PasswordInputField() {
        passwordField = new JBPasswordField();
        passwordField.getDocument().addDocumentListener(this);
        char defaultEchoChar = passwordField.getEchoChar();

        showPassword = new ClickableLabel(ShowPassword, () ->
                SwingUtilities.invokeLater(() -> {
                    if (isPasswordHidden) {
                        showPassword.setIcon(HidePassword);
                        showPassword.repaint();
                        isPasswordHidden = false;
                        passwordField.setEchoChar((char) 0);
                    } else {
                        showPassword.setIcon(ShowPassword);
                        showPassword.repaint();
                        isPasswordHidden = true;
                        passwordField.setEchoChar(defaultEchoChar);
                    }
                }));

        setLayout(new BorderLayout());
        add(passwordField, BorderLayout.CENTER);
        add(showPassword, BorderLayout.EAST);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        notifyListener();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        notifyListener();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        notifyListener();
    }

    public void setValue(Object value) {
        String valueAsString = converter.toText(value);
        passwordField.setText(valueAsString);
    }

    public void addListener(InputChangeListener listener) {
        this.listener = listener;
    }

    private void notifyListener() {
        if (listener != null) {
            char[] password = passwordField.getPassword();
            Object converted = converter.from(new String(password));
            listener.onChange(converted);
        }
    }
}
