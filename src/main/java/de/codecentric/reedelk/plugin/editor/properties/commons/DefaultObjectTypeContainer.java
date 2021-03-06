package de.codecentric.reedelk.plugin.editor.properties.commons;

import de.codecentric.reedelk.plugin.commons.TooltipContent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class DefaultObjectTypeContainer extends DisposablePanel {

    public static final Border BORDER_OBJECT_TYPE_CONTAINER_TOP = empty(5, 0);
    public static final Border BORDER_OBJECT_TYPE_CONTENT = empty(3, 10);

    DefaultObjectTypeContainer(JComponent renderedComponent, String displayName, TooltipContent tooltipContent) {
        setLayout(new BorderLayout());
        TypeObjectContainerHeader topHeader = new TypeObjectContainerHeader(displayName, tooltipContent);
        JPanel nestedContainerWrapper = ContainerFactory.pushCenter(renderedComponent, BORDER_OBJECT_TYPE_CONTENT);
        add(topHeader, NORTH);
        add(nestedContainerWrapper, CENTER);
        setBorder(BORDER_OBJECT_TYPE_CONTAINER_TOP);
    }
}
