package com.esb.plugin.designer.properties.widget;

import javax.swing.*;
import javax.swing.text.DocumentFilter;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public abstract class NumericInputField<T> extends InputField<T> {

    NumericInputField() {
        super();
        add(inputField, WEST);
        add(Box.createHorizontalBox(), CENTER);
        inputField.setColumns(numberOfColumns());
        document.setDocumentFilter(getDocumentFilter());
    }

    protected abstract int numberOfColumns();

    protected abstract DocumentFilter getDocumentFilter();

}
