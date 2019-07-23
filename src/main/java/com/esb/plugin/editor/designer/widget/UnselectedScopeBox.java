package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Colors;

import java.awt.*;

public class UnselectedScopeBox extends ScopeBox {

    public UnselectedScopeBox() {
        super(Colors.DESIGNER_UNSELECTED_SCOPE_BOX_BOUNDARIES,
                new BasicStroke(1f));
    }
}
