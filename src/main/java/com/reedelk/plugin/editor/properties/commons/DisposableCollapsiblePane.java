package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.TooltipContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.ArrowDown;
import static com.intellij.icons.AllIcons.General.ArrowRight;
import static com.reedelk.plugin.editor.properties.commons.PanelWithText.LoadingContentPanel;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class DisposableCollapsiblePane extends DisposablePanel {

    private final String displayName;
    private final DisposablePanel collapsedContent;
    private final transient TooltipContent tooltipContent;
    private final transient ContentRenderer renderingFunction;

    private DisposablePanel unCollapsedContent;

    private boolean collapsed = true;
    private boolean loaded = false;

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer) {
        this(displayName, unCollapsedContentRenderer, null, false);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     boolean defaultCollapsed) {
        this(displayName, unCollapsedContentRenderer, null, defaultCollapsed);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     @Nullable TooltipContent tooltipContent) {
        this(displayName, unCollapsedContentRenderer, tooltipContent, true);
    }

    public DisposableCollapsiblePane(@NotNull String displayName,
                                     @NotNull ContentRenderer unCollapsedContentRenderer,
                                     @Nullable TooltipContent tooltipContent,
                                     boolean defaultCollapsed) {
        this.displayName = displayName;
        this.tooltipContent = tooltipContent;
        this.renderingFunction = unCollapsedContentRenderer;
        this.collapsedContent = new CollapsedContent(displayName, tooltipContent);
        this.unCollapsedContent = new UnCollapsedContent(displayName, tooltipContent, new LoadingContentPanel());

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(DefaultObjectTypeContainer.BORDER_OBJECT_TYPE_CONTAINER_TOP);

        if (defaultCollapsed) {
            add(collapsedContent, CENTER);
        } else {
            add(unCollapsedContent, CENTER);
            lazyLoadUnCollapsedContent();
        }
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
            if (loaded) {
                removeAll();
                add(unCollapsedContent);
                revalidate();
            } else {
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
            removeAll();
            JComponent renderedContent = renderingFunction.render();
            loaded = true;
            unCollapsedContent = new UnCollapsedContent(displayName, tooltipContent, renderedContent);
            add(unCollapsedContent);
            revalidate();
        });
    }

    class UnCollapsedContent extends DisposablePanel {
        UnCollapsedContent(String displayName, TooltipContent tooltipContent, JComponent content) {
            // header
            TypeObjectContainerHeader topHeader =
                    new TypeObjectContainerHeader(displayName, tooltipContent, ArrowDown, clickAction);
            setLayout(new BorderLayout());
            setOpaque(false);
            add(topHeader, NORTH);
            add(content, CENTER);
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
