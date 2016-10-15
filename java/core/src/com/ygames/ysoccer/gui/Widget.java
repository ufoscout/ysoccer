package com.ygames.ysoccer.gui;

import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGraphics;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.math.Emath;

import java.awt.Color;
import java.util.List;

public abstract class Widget {

    // geometry
    public int x;
    public int y;
    public int w;
    public int h;

    public Image image;
    int imageX;
    int imageY;
    float imageScaleX;
    float imageScaleY;

    WidgetColor color;

    // text
    protected String text;
    Font font;
    Font.Align align;
    int textOffsetX;

    // state
    public boolean isActive;
    public boolean isSelected;
    protected boolean entryMode;
    public boolean isVisible;

    // misc
    protected boolean changed;

    public enum Event {
        NONE, FIRE1_DOWN, FIRE1_HOLD, FIRE1_UP, FIRE2_DOWN, FIRE2_HOLD, FIRE2_UP
    }

    public Widget() {
        imageScaleX = 1.0f;
        imageScaleY = 1.0f;
        color = new WidgetColor();
        align = Font.Align.CENTER;
        isVisible = true;
        changed = true;
    }

    public void update() {
    }

    public abstract void render(GlGraphics glGraphics);

    public void setGeometry(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setImagePosition(int imageX, int imageY) {
        this.imageX = imageX;
        this.imageY = imageY;
    }

    public void setImageScale(float scaleX, float scaleY) {
        imageScaleX = scaleX;
        imageScaleY = scaleY;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setColors(int body, int lightBorder, int darkBorder) {
        this.color.set(body, lightBorder, darkBorder);
    }

    public void setColors(Color body, Color lightBorder, Color darkBorder) {
        this.color.set(body.getRGB(), lightBorder.getRGB(), darkBorder.getRGB());
    }

    public void setColors(WidgetColor color) {
        this.color.set(color.body, color.lightBorder, color.darkBorder);
    }

    public void setColors(int color) {
        setColors(new Color(color));
    }

    public void setColors(Color color) {
        setColors(color.getRGB(), color.brighter().getRGB(), color.darker().getRGB());
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setText(int i) {
        setText("" + i);
    }

    public void setText(String text, Font.Align align, Font font) {
        this.text = text;
        this.align = align;
        this.font = font;
    }

    public void setText(int i, Font.Align align, Font font) {
        this.text = "" + i;
        this.align = align;
        this.font = font;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void onFire1Down() {
    }

    public void onFire1Hold() {
    }

    public void onFire1Up() {
    }

    public void onFire2Down() {
    }

    public void onFire2Hold() {
    }

    public void onFire2Up() {
    }

    public void onUpdate() {
    }

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public static void arrange(Settings settings, int centerY, int rowHeight, List<Widget> widgetList) {
        Widget w;
        int len = widgetList.size();
        int col1 = Emath.floor(len / 3.0) + ((len % 3) == 2 ? 1 : 0);
        int col2 = Emath.floor(len / 3.0) + ((len % 3) > 0 ? 1 : 0);
        for (int i = 0; i < len; i++) {
            w = widgetList.get(i);
            if (len <= 8) {
                w.x = (settings.GUI_WIDTH - w.w) / 2;
                w.y = centerY + rowHeight * (i - len / 2);
            } else {
                if (i < col1) {
                    w.x = (settings.GUI_WIDTH - 3 * w.w) / 2 - 20;
                    w.y = centerY + (int) (rowHeight * (i - col2 / 2.0));
                } else if (i < col1 + col2) {
                    w.x = (settings.GUI_WIDTH - w.w) / 2;
                    w.y = centerY + (int) (rowHeight * ((i - col1) - col2 / 2.0));
                } else {
                    w.x = (settings.GUI_WIDTH + w.w) / 2 + 20;
                    w.y = centerY + (int) (rowHeight * ((i - col1 - col2) - col2 / 2.0));
                }
            }
        }
    }

    public static int getRows(List<Widget> widgetList) {
        int len = widgetList.size();
        return len <= 8 ? len : Emath.floor(len / 3.0) + 1;
    }

}
