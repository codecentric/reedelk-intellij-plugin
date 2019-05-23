package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.converter.DoubleConverter;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.editor.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;

public class DoubleInputField extends NumericInputField<Double> {

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Double> getConverter() {
        return new DoubleConverter();
    }
}
