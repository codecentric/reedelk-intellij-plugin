package com.reedelk.plugin.editor.properties.widget.input;

import javax.swing.text.DocumentFilter;

public abstract class NumericInputField<T> extends InputField<T> {

    private static final int COLUMNS_NUMBER = 16;

    NumericInputField() {
        super();
        setColumns(COLUMNS_NUMBER);
        document.setDocumentFilter(getInputFilter());
    }

    protected abstract DocumentFilter getInputFilter();

}
