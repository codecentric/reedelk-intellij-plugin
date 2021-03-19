package de.codecentric.reedelk.plugin.editor.properties.selection;

public interface SelectionChangeListener {

    void onSelection(SelectableItem selectableItem);

    void unselect();

}
