package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.table.JBTable;

public interface DisposableTableColumnModelFactory {

    void create(JBTable table);
}
