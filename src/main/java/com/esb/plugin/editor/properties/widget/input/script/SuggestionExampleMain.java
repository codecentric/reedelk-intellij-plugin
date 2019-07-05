package com.esb.plugin.editor.properties.widget.input;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionExampleMain {

    public static void main(String[] args) {
        JFrame frame = createFrame();


        JTextPane textPane = new JTextPane();
        SuggestionDropDownDecorator.decorate(textPane,
                new TextComponentWordSuggestionClient(SuggestionExampleMain::getSuggestions));
        frame.add(new JScrollPane(textPane));
        frame.setVisible(true);
    }

    private static List<String> words = Arrays.asList("one","ona", "alf", "aub", "two", "three");

    private static List<String> getSuggestions(String input) {
        //the suggestion provider can control text search related stuff, e.g case insensitive match, the search  limit etc.
        if (input.isEmpty()) {
            return null;
        }
        return words.stream()
                .filter(s -> s.startsWith(input))
                .limit(20)
                .collect(Collectors.toList());
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Suggestion Dropdown Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 300));
        return frame;
    }
}
