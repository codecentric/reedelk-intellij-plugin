package com.reedelk.plugin.editor.properties.renderer.typebigdecimal;

import com.reedelk.plugin.commons.PluginValueConverterProvider;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;
import com.reedelk.runtime.converter.PluginValueConverter;

import javax.swing.text.DocumentFilter;
import java.math.BigDecimal;

public class BigDecimalInputField extends NumericInputField<BigDecimal> {

    public BigDecimalInputField(String hint) {
        super(hint);
    }

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
    protected PluginValueConverter<BigDecimal> getConverter() {
        return PluginValueConverterProvider.forType(BigDecimal.class);
    }
}
