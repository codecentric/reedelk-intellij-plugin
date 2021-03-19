package de.codecentric.reedelk.plugin.editor.properties.commons;

import javax.swing.text.DocumentFilter;

public abstract class NumericInputField<T> extends InputField<T> {

    private static final int COLUMNS_NUMBER = 16;

    public NumericInputField(String hint) {
        super(hint);
        setColumns(COLUMNS_NUMBER);
        document.setDocumentFilter(getInputFilter());
    }

    protected abstract DocumentFilter getInputFilter();

}
