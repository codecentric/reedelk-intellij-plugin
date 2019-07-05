package com.esb.plugin.editor.properties.widget.input;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public interface SuggestionClient<C extends JComponent> {

    Point getPopupLocation(C invoker);

    void setSelectedText(C invoker, String selectedValue);

    List<String> getSuggestions(C invoker);

}
