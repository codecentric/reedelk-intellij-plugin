package com.reedelk.plugin.editor.properties.renderer.typebigdecimal;

import com.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;

import java.math.BigDecimal;

public class BigDecimalPropertyRenderer extends AbstractNumericPropertyRenderer<BigDecimal> {
    @Override
    protected InputField<BigDecimal> getInputField(String hint) {
        return new BigDecimalInputField(hint);
    }
}
