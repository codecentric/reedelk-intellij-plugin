package com.esb.plugin.designer.properties.widget.input;

import javax.swing.*;
import javax.swing.text.DocumentFilter;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public abstract class NumericInputField<T> extends InputField<T> {

    private static final int COLUMNS_NUMBER = 16;

    NumericInputField() {
        super();
        add(inputField, WEST);
        add(Box.createHorizontalBox(), CENTER);
        inputField.setColumns(COLUMNS_NUMBER);
        document.setDocumentFilter(getInputFilter());
    }

    protected abstract DocumentFilter getInputFilter();

}
