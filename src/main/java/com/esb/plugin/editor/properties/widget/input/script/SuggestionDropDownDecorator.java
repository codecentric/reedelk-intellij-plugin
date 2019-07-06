package com.esb.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static java.awt.event.KeyEvent.*;

public class SuggestionDropDownDecorator {

    private final Document document;
    private final JTextComponent invoker;
    private final SuggestionClient suggestionClient;

    private JPopupMenu popupMenu = new JPopupMenu();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listComp = new JBList<>(listModel);
    private boolean disableTextEvent;

    public SuggestionDropDownDecorator(@NotNull JTextComponent invoker, Document document, @NotNull SuggestionClient suggestionClient) {
        this.invoker = invoker;
        this.suggestionClient = suggestionClient;

        this.listComp.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.listComp.setFocusable(false);

        Font font = new Font("Menlo", Font.PLAIN, 20);
        listComp.setFont(font);
        listComp.setSelectionBackground(new Color(159, 182, 198));
        listComp.setBackground(new Color(234, 243, 253));


        this.popupMenu.setLayout(new BorderLayout());
        this.popupMenu.setFocusable(false);
        this.popupMenu.setBackground(new Color(234, 243, 253));
        this.popupMenu.add(listComp, BorderLayout.WEST);

        this.document = document;
        document.addDocumentListener(new SuggestionDocumentListener(popupMenu));
        initInvokerKeyListeners(this.invoker);
    }

    public static void decorate(JTextComponent component, Document document, SuggestionClient suggestionClient) {
        new SuggestionDropDownDecorator(component, document, suggestionClient);
    }

    class SuggestionDocumentListener implements DocumentListener {

        private final JPopupMenu popupMenu;

        SuggestionDocumentListener(JPopupMenu popupMenu) {
            this.popupMenu = popupMenu;
        }

        @Override
        public void documentChanged(@NotNull com.intellij.openapi.editor.event.DocumentEvent event) {
            update(event);
        }

        private void update(com.intellij.openapi.editor.event.DocumentEvent e) {
            if (disableTextEvent) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                List<String> suggestions = suggestionClient.getSuggestions(invoker);
                if (suggestions != null && !suggestions.isEmpty()) {
                    this.popupMenu.setPopupSize(300, suggestions.size() * 33 + 8);
                    showPopup(suggestions);
                } else {
                    popupMenu.setVisible(false);
                }
            });
        }
    }

    private void showPopup(List<String> suggestions) {
        listModel.clear();
        suggestions.forEach(listModel::addElement);
        Point p = suggestionClient.getPopupLocation(invoker);
        if (p == null) {
            return;
        }
        popupMenu.pack();
        listComp.setSelectedIndex(0);
        popupMenu.show(invoker, (int) p.getX(), (int) p.getY());
    }

    private void initInvokerKeyListeners(JTextComponent invoker) {
        //not using key inputMap cause that would override the original handling
        invoker.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == VK_ENTER) {
                    selectFromList(e);
                } else if (e.getKeyCode() == VK_UP) {
                    moveUp(e);
                } else if (e.getKeyCode() == VK_DOWN) {
                    moveDown(e);
                } else if (e.getKeyCode() == VK_ESCAPE) {
                    popupMenu.setVisible(false);
                } else if (e.getKeyCode() == VK_BACK_SPACE) {
                    popupMenu.setVisible(false);
                }
            }
        });
    }

    private void selectFromList(KeyEvent e) {
        if (popupMenu.isVisible()) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex != -1) {
                popupMenu.setVisible(false);
                String selectedValue = listComp.getSelectedValue();
                disableTextEvent = true;
                suggestionClient.setSelectedText(invoker, document, selectedValue);
                disableTextEvent = false;
                e.consume();
            }
        }
    }

    private void moveDown(KeyEvent keyEvent) {
        if (popupMenu.isVisible() && listModel.getSize() > 0) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex < listModel.getSize()) {
                listComp.setSelectedIndex(selectedIndex + 1);
                keyEvent.consume();
            }
        }
    }

    private void moveUp(KeyEvent keyEvent) {
        if (popupMenu.isVisible() && listModel.getSize() > 0) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex > 0) {
                listComp.setSelectedIndex(selectedIndex - 1);
                keyEvent.consume();
            }
        }
    }
}
