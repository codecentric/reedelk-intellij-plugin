package com.esb.plugin.designer.properties.widget.input;

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
