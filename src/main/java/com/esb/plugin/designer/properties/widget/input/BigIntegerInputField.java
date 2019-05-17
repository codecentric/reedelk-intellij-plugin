package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.designer.properties.widget.NumericDocumentFilter;

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
        return ValueConverterFactory.forType(BigInteger.class);
    }
}
