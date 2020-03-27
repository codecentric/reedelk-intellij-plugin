package com.reedelk.plugin.editor.properties.renderer.typeinteger;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import com.reedelk.plugin.editor.properties.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.commons.NumericInputField;

import javax.swing.text.DocumentFilter;

public class IntegerInputField extends NumericInputField<Integer> {

    public IntegerInputField(String hint) {
        super(hint);
    }

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
        return ValueConverterProvider.forType(Integer.class);
    }
}
