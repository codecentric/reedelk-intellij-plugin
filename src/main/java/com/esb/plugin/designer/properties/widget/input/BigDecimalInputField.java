package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.BigDecimalConverter;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.designer.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;
import java.math.BigDecimal;

public class BigDecimalInputField extends NumericInputField<BigDecimal> {

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                new BigDecimal(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<BigDecimal> getConverter() {
        return new BigDecimalConverter();
    }
}
