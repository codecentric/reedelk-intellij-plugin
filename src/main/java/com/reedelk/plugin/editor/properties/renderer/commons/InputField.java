package com.reedelk.plugin.editor.properties.renderer.commons;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.reedelk.plugin.commons.HintPainter;
import com.reedelk.runtime.converter.PluginValueConverter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;

public abstract class InputField<T> extends JBTextField implements DocumentListener {

    private final String hint;
    private final transient PluginValueConverter<?> converter;
    private transient InputChangeListener listener;
    final PlainDocument document;

    InputField(String hint) {
        setForeground(JBColor.DARK_GRAY);
        this.converter = getConverter();
        this.document = (PlainDocument) getDocument();
        this.document.addDocumentListener(this);
        this.hint = hint;
    }

    InputField(String hint, int columns) {
        super(columns);
        setForeground(JBColor.DARK_GRAY);
        this.converter = getConverter();
        this.document = (PlainDocument) getDocument();
        this.document.addDocumentListener(this);
        this.hint = hint;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        HintPainter.from(g, this, hint);
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

    public Object getValue() {
        return converter.from(getText());
    }

    public void addListener(InputChangeListener changeListener) {
        this.listener = changeListener;
    }

    protected abstract PluginValueConverter<?> getConverter();

    private void notifyListener() {
        if (listener != null) {
            Object converted = converter.from(getText());
            listener.onChange(converted);
        }
    }
}
