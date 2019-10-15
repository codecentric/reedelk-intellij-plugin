package com.reedelk.plugin.editor.properties.widget.input;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.reedelk.plugin.converter.ValueConverter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

public abstract class InputField<T> extends JBTextField implements DocumentListener {

    final PlainDocument document;

    private final ValueConverter<?> converter;
    private final String hint;

    private InputChangeListener listener;

    InputField(String hint) {
        setForeground(JBColor.DARK_GRAY);
        converter = getConverter();
        document = (PlainDocument) getDocument();
        document.addDocumentListener(this);
        this.hint = hint;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintHint(g);
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

    protected abstract ValueConverter<?> getConverter();

    private void notifyListener() {
        if (listener != null) {
            Object converted = converter.from(getText());
            listener.onChange(converted);
        }
    }

    // TODO: Fix this code, it is horrible
    private void paintHint(Graphics g) {
        // Hint is painted if and only if it is not
        // null and the text it is empty.
        if (hint != null && getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(hint, ins.left + 5, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
}
