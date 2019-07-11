package com.esb.plugin.editor.properties.widget.input.script.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A trie or prefix tree, is an ordered tree structure, which takes advantage of the keys
 * that it stores – usually strings.
 *
 * A node’s position in the tree defines the key with which that node is associated, which
 * makes tries different in comparison to binary search trees, in which a node stores a
 * key that corresponds only to that node.
 *
 * All descendants of a node have a common prefix of a String associated with that node,
 * whereas the root is associated with an empty String.
 */
public class Trie {

    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren()
                    .computeIfAbsent(word.charAt(i), charAtI -> new TrieNode());
        }
        current.setEndOfWord(true);
    }

    public boolean delete(String word) {
        return delete(root, word, 0);
    }

    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Searches the given key in trie for a full match
     * and returns true on success else returns false.
     * @param word the word to search in the tree
     * @return true if the word was found, false otherwise.
     */
    public boolean search(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord();
    }

    public List<String> searchByPrefix(String prefix) {
        TrieNode current = root;
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

        if (notFound) return Collections.emptyList();
        else if (current.isEndOfWord() && current.getChildren().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> allWords = new ArrayList<>();

        int indexOfDot = tmpWord.lastIndexOf(".");
        String finalString = tmpWord.toString();
        if (indexOfDot > 0) {
            finalString = tmpWord.subSequence(indexOfDot + 1, tmpWord.length()).toString();
        }

        recursive(current, finalString, allWords);
        return allWords;
    }

    private void recursive(TrieNode current, String tmpWord, List<String> allWords) {
        if (current.isEndOfWord()) {
            allWords.add(tmpWord);
        }

        Map<Character, TrieNode> children = current.getChildren();
        for (Map.Entry<Character, TrieNode> child : children.entrySet()) {
            if (child.getKey() != '.') {
                recursive(child.getValue(), tmpWord + child.getKey(), allWords);
            }
        }
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode node = current.getChildren().get(ch);
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