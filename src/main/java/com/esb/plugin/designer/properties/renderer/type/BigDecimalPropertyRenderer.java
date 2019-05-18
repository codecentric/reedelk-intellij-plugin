package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.input.BigDecimalInputField;
import com.esb.plugin.designer.properties.widget.input.InputField;

import java.math.BigDecimal;

public class BigDecimalPropertyRenderer extends NumericPropertyRenderer<BigDecimal> {

    @Override
    protected InputField<BigDecimal> getInputField() {
        return new BigDecimalInputField();
    }
}
