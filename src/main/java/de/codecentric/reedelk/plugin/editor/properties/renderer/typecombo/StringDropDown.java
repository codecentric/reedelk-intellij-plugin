package de.codecentric.reedelk.plugin.editor.properties.renderer.typecombo;

import de.codecentric.reedelk.plugin.editor.properties.CommitPropertiesListener;
import de.codecentric.reedelk.plugin.editor.properties.commons.InputChangeListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import de.codecentric.reedelk.plugin.commons.Topics;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class StringDropDown extends JComboBox<String> implements ItemListener, CommitPropertiesListener, Disposable {

    private final transient MessageBusConnection connect;
    private transient InputChangeListener listener;

    public StringDropDown(Module module, String[] items, boolean editable, String prototype) {
        super(items);
        setEditable(editable);
        if (editable) {
            JTextField field = (JTextField) getEditor().getEditorComponent();
            field.addKeyListener(new ComboDropdownSuggestion(this));
        }
        if (prototype != null) {
            setPrototypeDisplayValue(prototype);
        }
        addItemListener(this);

        this.connect = module.getProject().getMessageBus().connect();
        this.connect.subscribe(Topics.COMMIT_COMPONENT_PROPERTIES_EVENTS, this);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED && listener != null) {
            String item = (String) event.getItem();
            listener.onChange(item);
        }
    }

    public void addListener(InputChangeListener changeListener) {
        this.listener = changeListener;
    }

    public void setValue(Object value) {
        setSelectedItem(value);
    }

    @Override
    public void dispose() {
        if (connect != null) {
            connect.disconnect();
        }
    }

    @Override
    public void onCommit() {
        // If it is editable, then we notify a change with the currently
        // typed in value. If it is not editable the user must explicitly
        // select an item, which would in turn trigger 'itemStateChanged' above.
        if (isEditable() && listener != null) {
            String item = (String) getEditor().getItem();
            listener.onChange(item);
        }
    }
}
