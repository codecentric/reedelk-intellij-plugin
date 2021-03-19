package de.codecentric.reedelk.plugin.editor.designer.scopebox;

import de.codecentric.reedelk.plugin.commons.Colors;

import java.awt.*;

public class UnselectedScopeBox extends ScopeBox {

    public UnselectedScopeBox() {
        super(Colors.DESIGNER_UNSELECTED_SCOPE_BOX_BOUNDARIES,
                new BasicStroke(1f));
    }
}
