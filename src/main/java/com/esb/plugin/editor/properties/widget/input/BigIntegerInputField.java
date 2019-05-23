package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.converter.BigIntegerConverter;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.editor.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;
import java.math.BigInteger;

public class BigIntegerInputField extends NumericInputField<BigInteger> {

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                new BigInteger(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<BigInteger> getConverter() {
        return new BigIntegerConverter();
    }
}
