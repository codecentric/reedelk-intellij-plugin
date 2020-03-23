package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.TooltipContent;
import com.reedelk.plugin.editor.properties.commons.*;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.ArrowDown;
import static com.intellij.icons.AllIcons.General.ArrowRight;
import static com.reedelk.plugin.editor.properties.commons.PanelWithText.LoadingContentPanel;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class CollapsibleObjectTypeContainer extends DisposablePanel {

    private final String displayName;
    private final DisposablePanel collapsedContent;
    private final transient TooltipContent tooltipContent;
    private final transient ContainerRenderingFunction renderingFunction;

    private DisposablePanel unCollapsedContent;

    private boolean collapsed = true;
    private boolean loaded = false;

    CollapsibleObjectTypeContainer(String displayName, TooltipContent tooltipContent, ContainerRenderingFunction unCollapsedContentRenderer) {
        this.renderingFunction = unCollapsedContentRenderer;
        this.displayName = displayName;
        this.tooltipContent = tooltipContent;
        this.collapsedContent = new CollapsedContent(displayName, tooltipContent);
        this.unCollapsedContent = new UnCollapsedContent(displayName, tooltipContent, new LoadingContentPanel());

        setLayout(new BorderLayout());
        add(collapsedContent, CENTER);
        setBorder(DefaultObjectTypeContainer.BORDER_OBJECT_TYPE_CONTAINER_TOP);
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
            unCollapsedContent = new UnCollapsedContent(displayName, tooltipContent, renderedContent);
            add(unCollapsedContent);
            revalidate();
        });
    }

    class UnCollapsedContent extends DisposablePanel {
        UnCollapsedContent(String displayName, TooltipContent tooltipContent, JComponent content) {
            TypeObjectContainerHeader topHeader = new TypeObjectContainerHeader(displayName, tooltipContent, ArrowDown, clickAction);

            JPanel nestedContainerWrapper =
                    ContainerFactory.pushCenter(content, DefaultObjectTypeContainer.BORDER_OBJECT_TYPE_CONTENT);

            setLayout(new BorderLayout());
            add(topHeader, NORTH);
            add(nestedContainerWrapper, CENTER);
        }
    }

    class CollapsedContent extends TypeObjectContainerHeader {
        CollapsedContent(String displayName, TooltipContent tooltipContent) {
            super(displayName, tooltipContent, ArrowRight, clickAction);
        }
    }

    private final transient ClickableLabel.OnClickAction clickAction = () -> {
        if (collapsed) {
            unCollapse();
            collapsed = false;
        } else {
            collapse();
            collapsed = true;
        }
    };
}
