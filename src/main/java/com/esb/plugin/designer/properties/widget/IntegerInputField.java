package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;

import javax.swing.text.DocumentFilter;

public class IntegerInputField extends NumericInputField<Integer> {

    private static final int COLUMNS_NUMBER = 14;

    @Override
    protected ValueConverter<Integer> getConverter() {
        return ValueConverterFactory.forType(Integer.class);
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return new IntegerDocumentFilter();
    }

    @Override
    protected int numberOfColumns() {
        return COLUMNS_NUMBER;
    }

    static class IntegerDocumentFilter extends NumericDocumentFilter {

        @Override
        protected boolean test(String text) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
