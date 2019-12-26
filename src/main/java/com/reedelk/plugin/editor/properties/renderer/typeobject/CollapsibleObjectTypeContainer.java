package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.intellij.ui.JBColor;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerRenderingFunction;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.LoadingContentPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.ArrowDown;
import static com.intellij.icons.AllIcons.General.ArrowRight;
import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.intellij.util.ui.JBUI.Borders.emptyTop;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.IconAlignment.RIGHT;
import static java.awt.BorderLayout.*;
import static javax.swing.BorderFactory.createMatteBorder;

class CollapsibleObjectTypeContainer extends DisposablePanel {

    private final String displayName;
    private final DisposablePanel collapsedContent;
    private final ContainerRenderingFunction renderingFunction;
    private DisposablePanel unCollapsedContent;

    private boolean collapsed = true;
    private boolean loaded = false;

    CollapsibleObjectTypeContainer(String displayName, ContainerRenderingFunction unCollapsedContentRenderer) {
        this.renderingFunction = unCollapsedContentRenderer;
        this.displayName = displayName;
        this.collapsedContent = new CollapsedContent(displayName);
        this.unCollapsedContent = new UnCollapsedContent(displayName, new LoadingContentPanel());

        setLayout(new BorderLayout());
        add(collapsedContent, CENTER);
        setBorder(BORDER_COLLAPSIBLE_OBJECT_CONTAINER);
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(collapsedContent);
        DisposableUtils.dispose(unCollapsedContent);
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
            if (!loaded) {
                lazyLoadUnCollapsedContent();
            }
        });
    }

    /**
     * Loads the content of the unCollapsed Panel by using the provided rendering
     * function. This is needed to improve rendering performances when component's
     * properties are displayed in the Properties Tool Window.
     */
    private void lazyLoadUnCollapsedContent() {
        SwingUtilities.invokeLater(() -> {
            remove(unCollapsedContent);
            JComponent renderedContent = renderingFunction.render();
            loaded = true;
            unCollapsedContent = new UnCollapsedContent(displayName, renderedContent);
            add(unCollapsedContent);
            revalidate();
        });
    }

    class UnCollapsedContent extends DisposablePanel {
        UnCollapsedContent(String displayName, JComponent content) {
            TopBar topBar = new TopBar(displayName, ArrowDown);

            JPanel nestedContainerWrapper = new DisposablePanel();
            nestedContainerWrapper.setLayout(new BorderLayout());
            nestedContainerWrapper.add(content, CENTER);
            nestedContainerWrapper.setBorder(BORDER_UN_COLLAPSED_CONTENT);

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
        return new ClickableLabel(displayName, icon, RIGHT, () -> {
            if (collapsed) {
                unCollapse();
                collapsed = false;
            } else {
                collapse();
                collapsed = true;
            }
        });
    }

    static class HorizontalSeparator extends DisposablePanel {
        HorizontalSeparator(JBColor color) {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JSeparator jSeparator = new JSeparator();
            jSeparator.setForeground(color);
            add(jSeparator, gbc);

            setBorder(BORDER_HORIZONTAL_SEPARATOR);
        }
    }

    static final Border BORDER_COLLAPSIBLE_OBJECT_CONTAINER = emptyTop(5);
    static final Border BORDER_HORIZONTAL_SEPARATOR = empty(2, 5, 0, 0);
    static final Border BORDER_UN_COLLAPSED_CONTENT_OUTSIDE = createMatteBorder(0, 1, 1, 1, Colors.CONTAINER_OBJECT_TYPE_COLLAPSIBLE_BORDER);
    static final Border BORDER_UN_COLLAPSED_CONTENT_INSIDE = new CompoundBorder(BORDER_UN_COLLAPSED_CONTENT_OUTSIDE, empty(2, 4, 4, 0));
    static final Border BORDER_UN_COLLAPSED_CONTENT = new CompoundBorder(empty(0, 8, 8, 0), BORDER_UN_COLLAPSED_CONTENT_INSIDE);
}