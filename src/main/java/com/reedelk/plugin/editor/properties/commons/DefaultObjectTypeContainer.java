package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.commons.TooltipContent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.intellij.util.ui.JBUI.Borders.emptyTop;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class DefaultObjectTypeContainer extends DisposablePanel {

    public static final Border BORDER_OBJECT_TYPE_CONTAINER_TOP = emptyTop(5);
    public static final Border BORDER_OBJECT_TYPE_CONTENT = empty(3, 10);

    DefaultObjectTypeContainer(JComponent renderedComponent, String displayName, TooltipContent tooltipContent) {
        setLayout(new BorderLayout());

        TypeObjectContainerHeader topHeader = new TypeObjectContainerHeader(displayName, tooltipContent);
        add(topHeader, NORTH);

        JPanel nestedContainerWrapper = ContainerFactory.pushCenter(renderedComponent, BORDER_OBJECT_TYPE_CONTENT);

        add(nestedContainerWrapper, CENTER);
    }
}
