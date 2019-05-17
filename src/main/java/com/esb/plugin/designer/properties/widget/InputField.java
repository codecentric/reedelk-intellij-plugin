package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.designer.properties.InputChangeListener;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;

public abstract class InputField<T> extends JBPanel implements DocumentListener {

    final JBTextField inputField;
    final PlainDocument document;

    private final ValueConverter<T> converter;

    private InputChangeListener<T> listener;

    InputField() {
        super(new BorderLayout());
        inputField = new JBTextField();

        converter = getConverter();

        document = (PlainDocument) inputField.getDocument();
        document.addDocumentListener(this);
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

    private void notifyListener() {
        if (listener != null) {
            T objectValue = converter.from(inputField.getText());
            listener.onChange(objectValue);
        }
    }

    public void addListener(InputChangeListener<T> changeListener) {
        this.listener = changeListener;
    }

    public void setValue(Object value) {
        String valueAsString = converter.toString(value);
        inputField.setText(valueAsString);
    }


    protected abstract ValueConverter<T> getConverter();

}
