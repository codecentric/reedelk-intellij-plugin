package com.esb.plugin.component.choice.widget;

import com.esb.plugin.designer.properties.widget.FormUtility;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;

public class AddRouteCondition extends JBPanel {

    public AddRouteCondition() {
        setLayout(new GridBagLayout());

        FormUtility formUtility = new FormUtility();
        formUtility.addLabel("Condition:", this);
        formUtility.addMiddleField(new JBTextField(), this);

        JButton btnAddCondition = new JButton("Add");
        btnAddCondition.setSize(new Dimension(50, 20));

        JBPanel selectRouteBox = new JBPanel();
        selectRouteBox.setLayout(new GridBagLayout());

        formUtility.addLabel("Route:", selectRouteBox);
        formUtility.addMiddleField(ROUTES, selectRouteBox);
        formUtility.addLabel(btnAddCondition, selectRouteBox);
        formUtility.addLastField(selectRouteBox, this);
    }

    private static final JComboBox<String> ROUTES = new ComboBox<>();

    static {
        ROUTES.addItem("Route1");
        ROUTES.addItem("Route2");
        ROUTES.addItem("Route3");
        ROUTES.addItem("Route4");
    }
}
