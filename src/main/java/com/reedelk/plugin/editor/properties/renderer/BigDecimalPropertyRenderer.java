package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.BigDecimalInputField;
import com.reedelk.plugin.editor.properties.widget.input.InputField;

import java.math.BigDecimal;

public class BigDecimalPropertyRenderer extends NumericPropertyRenderer<BigDecimal> {
    @Override
    protected InputField<BigDecimal> getInputField() {
        return new BigDecimalInputField();
    }
}
