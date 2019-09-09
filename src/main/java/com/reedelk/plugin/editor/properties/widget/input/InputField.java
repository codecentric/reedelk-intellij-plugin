package com.reedelk.plugin.editor.properties.widget.input;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.reedelk.plugin.converter.ValueConverter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;

public abstract class InputField<T> extends JBTextField implements DocumentListener {

    final PlainDocument document;

    private final ValueConverter<T> converter;
    private final String hint;

    private InputChangeListener<T> listener;

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
        if (hint != null) {
            paintHint(g);
        }
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

    private void paintHint(Graphics g) {
        if (getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
