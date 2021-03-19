package de.codecentric.reedelk.plugin.editor.palette;

import com.intellij.ui.JBColor;
import de.codecentric.reedelk.plugin.commons.Colors;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class DefaultTreeNodeCellRenderer extends DefaultTreeCellRenderer {

    public DefaultTreeNodeCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Color getBackgroundSelectionColor() {
        return null;
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return JBColor.background();
    }

    @Override
    public Color getTextNonSelectionColor() {
        return Colors.PALETTE_TEXT_UNSELECTED;
    }

    @Override
    public Color getTextSelectionColor() {
        return Colors.PALETTE_TEXT_SELECTED;
    }
}
