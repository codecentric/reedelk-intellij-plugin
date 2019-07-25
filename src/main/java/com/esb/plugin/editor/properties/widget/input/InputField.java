package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.converter.ValueConverter;
import com.intellij.ui.components.JBTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

public abstract class InputField<T> extends JBTextField implements DocumentListener {

    final PlainDocument document;

    private final ValueConverter<T> converter;

    private InputChangeListener<T> listener;

    InputField() {
        setBackground(Colors.PROPERTIES_BACKGROUND);
        converter = getConverter();
        document = (PlainDocument) getDocument();
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

    public void setValue(T value) {
        String valueAsString = converter.toText(value);
        setText(valueAsString);
    }

    public void addListener(InputChangeListener<T> changeListener) {
        this.listener = changeListener;
    }

    protected abstract ValueConverter<T> getConverter();

    private void notifyListener() {
        if (listener != null) {
            T objectValue = converter.from(getText());
            listener.onChange(objectValue);
        }
    }

}
