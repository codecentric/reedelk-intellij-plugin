package com.reedelk.plugin.editor.properties.renderer.typelist.custom;

import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.TableModelDefaultAbstract;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ListTableCustomModel extends TableModelDefaultAbstract {

    private final transient PropertyAccessor propertyAccessor;

    public ListTableCustomModel(PropertyAccessor propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
        List<ObjectDescriptor.TypeObject> data = propertyAccessor.get();
        if (data != null) {
            data.forEach(typeObject -> addRow(new Object[]{typeObject}));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getValueAt(int row, int column) {
        return ((Vector<ObjectDescriptor.TypeObject>)dataVector.elementAt(row)).get(0); // 1 is the value
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onUpdate(Vector<?> data) {
        List<ObjectDescriptor.TypeObject> updated = new ArrayList<>();
        data.forEach(vector -> {
            ObjectDescriptor.TypeObject value = ((Vector<ObjectDescriptor.TypeObject>)vector).get(0); // 1 is the value
            updated.add(value);
        });
        propertyAccessor.set(updated);
    }

    @Override
    public Object[] createRow() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }
}
