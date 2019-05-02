package com.esb.plugin.component.choice.widget;

import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddConditionRoute extends JBPanel implements ActionListener {

    private JBPanel selectRouteBox;
    private JButton btnAddCondition;
    private JTextField conditionInput;
    private JComboBox<String> routesCombo;
    private AddConditionRouteListener listener;

    public AddConditionRoute(JComboBox<String> routesCombo) {
        setLayout(new GridBagLayout());

        this.routesCombo = routesCombo;

        conditionInput = new JBTextField();

        selectRouteBox = new JBPanel();
        selectRouteBox.setLayout(new GridBagLayout());

        btnAddCondition = new JButton("Add");
        btnAddCondition.setActionCommand("add");
        btnAddCondition.setSize(new Dimension(50, 20));
        btnAddCondition.addActionListener(this);

        FormBuilder.get()
                .addLabel("Condition:", this)
                .addMiddleField(conditionInput, this)
                .addLabel("Route:", selectRouteBox)
                .addMiddleField(routesCombo, selectRouteBox)
                .addLabel(btnAddCondition, selectRouteBox)
                .addLastField(selectRouteBox, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (listener != null) {
            ConditionRoutePair pair = new ConditionRoutePair(
                    conditionInput.getText(), (String)
                    routesCombo.getSelectedItem());
            listener.addRouteCondition(pair);
        }
    }

    public void addListener(AddConditionRouteListener listener) {
        this.listener = listener;
    }

}
