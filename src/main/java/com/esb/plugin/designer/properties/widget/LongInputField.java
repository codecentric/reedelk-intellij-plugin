package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;

import javax.swing.text.DocumentFilter;

public class LongInputField extends NumericInputField<Long> {

    private static final int COLUMNS_NUMBER = 16;

    @Override
    protected ValueConverter<Long> getConverter() {
        return ValueConverterFactory.forType(Long.class);
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Long.parseLong(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected int numberOfColumns() {
        return COLUMNS_NUMBER;
    }

}
