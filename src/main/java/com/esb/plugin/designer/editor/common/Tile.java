package com.esb.plugin.designer.editor.common;

public class Tile {

    public static final Tile INSTANCE = new Tile(100, 100);

    public final int width;
    public final int height;

    private Tile(int width, int height) {
        this.width = width;
        this.height = height;
    }

}
