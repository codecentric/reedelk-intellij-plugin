package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A trie or prefix tree, is an ordered tree structure, which takes advantage of the keys
 * that it stores – usually strings.
 * <p>
 * A node’s position in the tree defines the key with which that node is associated, which
 * makes tries different in comparison to binary search trees, in which a node stores a
 * key that corresponds only to that node.
 * <p>
 * All descendants of a node have a common prefix of a String associated with that node,
 * whereas the root is associated with an empty String.
 */
public class SuggestionTree {

    private SuggestionTreeNode root;

    public SuggestionTree() {
        root = new SuggestionTreeNode();
    }

    public void insert(SuggestionToken suggestionToken) {
        SuggestionTreeNode current = root;
        String word = suggestionToken.value;

        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren()
                    .computeIfAbsent(word.charAt(i), charAtI -> new SuggestionTreeNode());
        }
        current.setEndOfWord(true);
        current.setType(suggestionToken.type);
    }

    public boolean delete(String word) {
        return delete(root, word, 0);
    }

    public boolean isEmpty() {
        return root == null;
    }

    @NotNull
    public Set<Suggestion> searchByPrefix(String prefix) {
        SuggestionTreeNode current = root;
        boolean notFound = false;
        StringBuilder tmpWord = new StringBuilder();

        for (int i = 0; i < prefix.length(); i++) {
            char a = prefix.charAt(i);
            if (!current.getChildren().containsKey(a)) {
                notFound = true;
                break;
            }
            tmpWord.append(a);
            current = current.getChildren().get(a);
        }

        if (notFound) return Collections.emptySet();
        else if (current.isEndOfWord() && current.getChildren().isEmpty()) {
            return Collections.emptySet();
        }

        Set<Suggestion> allWords = new HashSet<>();

        int indexOfDot = tmpWord.lastIndexOf(".");
        String finalString = tmpWord.toString();
        if (indexOfDot > 0) {
            finalString = tmpWord.subSequence(indexOfDot + 1, tmpWord.length()).toString();
        }

        recursive(current, finalString, allWords);
        return allWords;
    }

    private void recursive(SuggestionTreeNode current, String tmpWord, Set<Suggestion> allWords) {
        if (current.isEndOfWord()) {
            allWords.add(new Suggestion(current.getType(), tmpWord));
        }

        Map<Character, SuggestionTreeNode> children = current.getChildren();
        for (Map.Entry<Character, SuggestionTreeNode> child : children.entrySet()) {
            if (child.getKey() != '.') {
                recursive(child.getValue(), tmpWord + child.getKey(), allWords);
            } else {
                allWords.add(new Suggestion(current.getType(), tmpWord));
            }
        }
    }

    private boolean delete(SuggestionTreeNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }
        char ch = word.charAt(index);
        SuggestionTreeNode node = current.getChildren().get(ch);
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }
        return false;
    }
}