package com.reedelk.plugin.editor.properties.renderer.commons;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

public abstract class InputField<T> extends JBTextField implements DocumentListener {

    private final Color hintColor;

    final PlainDocument document;


    private final transient ValueConverter<?> converter;
    private final String hint;

    private transient InputChangeListener listener;

    InputField(String hint) {
        setForeground(JBColor.DARK_GRAY);
        this.converter = getConverter();
        this.document = (PlainDocument) getDocument();
        this.document.addDocumentListener(this);
        this.hint = hint;

        int c0 = getBackground().getRGB();
        int c1 = getForeground().getRGB();
        int m = 0xfefefefe;
        int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
        hintColor = new Color(c2, true);
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

    protected abstract ValueConverter getConverter();

    private void notifyListener() {
        if (listener != null) {
            Object converted = converter.from(getText());
            listener.onChange(converted);
        }
    }

    private void paintHint(Graphics g) {
        // Hint is painted if and only if it is present
        if (StringUtils.isNotBlank(hint) && getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            g.setColor(hintColor);
            g.drawString(hint, ins.left + 5, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
}
