package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.Font;

public abstract class Widget {

    // geometry
    int x;
    int y;
    int w;
    int h;

    // colors
    int body;
    int lightBorder;
    int darkBorder;

    // text
    String text;
    Font font;
    Font.Align align;
    int textOffsetX;

    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setColors(int body, int lightBorder, int darkBorder) {
        this.body = body;
        this.lightBorder = lightBorder;
        this.darkBorder = darkBorder;
    }

    public void setText(String text, Font.Align align, Font font) {
        this.text = text;
        this.align = align;
        this.font = font;
    }
}
