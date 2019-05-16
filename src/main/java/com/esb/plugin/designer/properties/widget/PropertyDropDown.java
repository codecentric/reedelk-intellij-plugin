package com.esb.plugin.designer.properties.widget;

import javax.swing.*;
import java.util.List;

public class PropertyDropDown extends JComboBox<String> {

    public PropertyDropDown(List<String> values) {
        super(values.toArray(new String[0]));
    }
}
