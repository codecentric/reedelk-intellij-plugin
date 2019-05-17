package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.converter.ValueConverter;

import javax.swing.text.DocumentFilter;

public class IntegerInputField extends InputField<Integer> {

    private static final int COLUMNS_NUMBER = 14;

    @Override
    protected int numberOfColumns() {
        return COLUMNS_NUMBER;
    }

    @Override
    protected ValueConverter<Integer> getConverter() {
        return null;
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return null;
    }
}
