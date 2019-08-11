package com.reedelk.plugin.editor.properties.widget.input;

import com.reedelk.plugin.converter.IntegerConverter;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.editor.properties.widget.NumericDocumentFilter;

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
