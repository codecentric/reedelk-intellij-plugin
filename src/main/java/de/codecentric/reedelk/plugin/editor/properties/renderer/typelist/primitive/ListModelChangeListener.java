package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.primitive;

import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class ListModelChangeListener implements ListDataListener {

    private final PropertyAccessor propertyAccessor;

    public ListModelChangeListener(PropertyAccessor propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
    }

    @Override
    public void intervalAdded(ListDataEvent event) {
        updateAccessorData(event);
    }

    @Override
    public void intervalRemoved(ListDataEvent event) {
        updateAccessorData(event);
    }

    @Override
    public void contentsChanged(ListDataEvent event) {
        updateAccessorData(event);
    }

    @SuppressWarnings("unchecked")
    private void updateAccessorData(ListDataEvent event) {
        DefaultListModel<Object> model = (DefaultListModel<Object>) event.getSource();
        List<Object> newList = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            newList.add(model.getElementAt(i));
        }
        propertyAccessor.set(newList);
    }
}
