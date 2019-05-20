package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.IntegerConverter;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.designer.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;

public class IntegerInputField extends NumericInputField<Integer> {

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Integer> getConverter() {
        return new IntegerConverter();
    }

}
