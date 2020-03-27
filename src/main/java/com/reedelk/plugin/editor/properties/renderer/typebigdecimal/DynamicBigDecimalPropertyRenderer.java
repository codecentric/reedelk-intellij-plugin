package com.reedelk.plugin.editor.properties.renderer.typebigdecimal;

import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldBaseAdapter;
import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

public class DynamicBigDecimalPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new BigDecimalInputField(hint));
    }
}
