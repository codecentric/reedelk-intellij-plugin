package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.intellij.ui.JBColor;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.ArrowDown;
import static com.intellij.icons.AllIcons.General.ArrowRight;
import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.IconAlignment;
import static java.awt.BorderLayout.*;

class CollapsibleObjectTypeContainer extends DisposablePanel {

    private final DisposablePanel collapsedContent;
    private final DisposablePanel unCollapsedContent;

    private boolean collapsed = true;

    CollapsibleObjectTypeContainer(JComponent visiblePanel, String displayName) {
        this.unCollapsedContent = new UnCollapsedContent(displayName, visiblePanel);
        this.collapsedContent = new CollapsedContent(displayName);

        setLayout(new BorderLayout());
        add(collapsedContent, CENTER);
        setBorder(Borders.emptyTop(5));
    }

    @Override
    public void dispose() {
        super.dispose();
        collapsedContent.dispose();
        unCollapsedContent.dispose();
    }

    private void collapse() {
        SwingUtilities.invokeLater(() -> {
            remove(unCollapsedContent);
            add(collapsedContent);
            revalidate();
        });
    }

    private void unCollapse() {
        SwingUtilities.invokeLater(() -> {
            remove(collapsedContent);
            add(unCollapsedContent);
            revalidate();
        });
    }

    class UnCollapsedContent extends DisposablePanel {
        UnCollapsedContent(String displayName, JComponent content) {
            TopBar topBar = new TopBar(displayName, ArrowDown);

            MatteBorder contentBorder = BorderFactory.createMatteBorder(0, 1, 1, 1, Colors.CONTAINER_OBJECT_TYPE_COLLAPSIBLE_BORDER);
            CompoundBorder externalBorder = new CompoundBorder(contentBorder, Borders.empty(2, 4, 4, 0));
            CompoundBorder borderWithPadding = new CompoundBorder(Borders.empty(0, 8, 8, 0), externalBorder);

            JPanel nestedContainerWrapper = new DisposablePanel();
            nestedContainerWrapper.setLayout(new BorderLayout());
            nestedContainerWrapper.add(content, CENTER);
            nestedContainerWrapper.setBorder(borderWithPadding);

            setLayout(new BorderLayout());
            add(topBar, NORTH);
            add(nestedContainerWrapper, CENTER);
        }
    }

    class CollapsedContent extends TopBar {
        CollapsedContent(String displayName) {
            super(displayName, ArrowRight);
        }
    }

    class TopBar extends DisposablePanel {
        TopBar(String displayName, Icon icon) {
            JLabel switchLabel = createSwitch(icon, displayName);
            HorizontalSeparator separator = new HorizontalSeparator(JBColor.LIGHT_GRAY);
            setLayout(new BorderLayout());
            add(switchLabel, WEST);
            add(separator, CENTER);
        }
    }

    private JLabel createSwitch(Icon icon, String displayName) {
        return new ClickableLabel(displayName, icon, icon, IconAlignment.RIGHT, () -> {
            if (collapsed) {
                unCollapse();
                collapsed = false;
            } else {
                collapse();
                collapsed = true;
            }
        });
    }
}
