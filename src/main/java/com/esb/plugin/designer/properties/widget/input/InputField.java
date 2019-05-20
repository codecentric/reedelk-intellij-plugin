package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.intellij.ui.components.JBTextField;

import javax.swing.text.PlainDocument;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public abstract class InputField<T> extends JBTextField implements FocusListener {

    final PlainDocument document;

    private final ValueConverter<T> converter;

    private InputChangeListener<T> listener;

    InputField() {
        converter = getConverter();
        document = (PlainDocument) getDocument();
        addFocusListener(this);
    }

    @Override
    public void focusLost(FocusEvent e) {
        notifyListener();
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    public void setValue(Object value) {
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
