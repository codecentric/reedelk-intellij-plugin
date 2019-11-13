package com.reedelk.plugin.editor.designer.scopebox;

import com.reedelk.plugin.commons.Colors;

import java.awt.*;

public class UnselectedScopeBox extends ScopeBox {

    public UnselectedScopeBox() {
        super(Colors.DESIGNER_UNSELECTED_SCOPE_BOX_BOUNDARIES,
                new BasicStroke(1f));
    }
}
