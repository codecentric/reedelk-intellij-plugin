package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.designer.properties.InputChangeListener;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public abstract class NumericInputField<T> extends JBPanel implements DocumentListener {

    private final JBTextField inputField;
    private final ValueConverter<T> converter;

    private InputChangeListener<T> listener;

    NumericInputField() {
        super(new BorderLayout());
        inputField = new JBTextField(numberOfColumns());

        converter = getConverter();

        PlainDocument document = (PlainDocument) inputField.getDocument();
        document.setDocumentFilter(getDocumentFilter());
        document.addDocumentListener(this);

        add(inputField, WEST);
        add(Box.createHorizontalBox(), CENTER);
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

    protected abstract int numberOfColumns();

    protected abstract ValueConverter<T> getConverter();

    protected abstract DocumentFilter getDocumentFilter();

}
