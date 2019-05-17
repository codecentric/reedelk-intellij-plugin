package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.converter.ValueConverter;

import javax.swing.text.DocumentFilter;
import java.math.BigDecimal;

public class BigDecimalInputField extends NumericInputField<BigDecimal> {

    @Override
    protected int numberOfColumns() {
        return 0;
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return null;
    }

    @Override
    protected ValueConverter<BigDecimal> getConverter() {
        return null;
    }
}
