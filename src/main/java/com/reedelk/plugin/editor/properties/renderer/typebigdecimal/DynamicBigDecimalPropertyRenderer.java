package com.reedelk.plugin.editor.properties.renderer.typebigdecimal;

import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldBaseAdapter;

public class DynamicBigDecimalPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new BigDecimalInputField(hint));
    }
}
