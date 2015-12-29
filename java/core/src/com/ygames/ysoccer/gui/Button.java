package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.GlColor;
import com.ygames.ysoccer.framework.GlGraphics;

public class Button extends Widget {

    private static final float alpha = 0.9f;

    @Override
    public void render(GlGraphics glGraphics) {
        ShapeRenderer shapeRenderer = glGraphics.shapeRenderer;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // body (0x000000 = invisible)
        if (body != 0x000000) {
            shapeRenderer.setColor(GlColor.red(body) / 255f, GlColor.green(body) / 255f, GlColor.blue(body) / 255f, alpha);
            shapeRenderer.rect(x + 2, y + 2, w - 4, h - 4);
        }

        // border ($000000 = invisible)
        if (lightBorder != 0x000000) {
            drawBorder(shapeRenderer, x, y, w, h, lightBorder, darkBorder);
        }

        shapeRenderer.end();

        drawText(glGraphics);
    }

    private void drawBorder(ShapeRenderer shapeRenderer, int bx, int by, int bw,
                            int bh, int topLeftColor, int bottomRightColor) {

        // top left border
        shapeRenderer.setColor(GlColor.red(topLeftColor) / 255f, GlColor.green(topLeftColor) / 255f, GlColor.blue(topLeftColor) / 255f, alpha);
        shapeRenderer.triangle(bx, by, bx + bw, by, bx + bw - 1, by + 2);
        shapeRenderer.triangle(bx + bw - 1, by + 2, bx + 1, by + 2, bx, by);
        shapeRenderer.triangle(bx, by + 1, bx, by + bh, bx + 2, by + bh - 1);
        shapeRenderer.triangle(bx + 2, by + bh - 1, bx + 2, by + 2, bx, by + 1);

        // bottom right border
        shapeRenderer.setColor(GlColor.red(bottomRightColor) / 255f, GlColor.green(bottomRightColor) / 255f, GlColor.blue(bottomRightColor) / 255f, alpha);
        shapeRenderer.triangle(bx + bw - 2, by + 2, bx + bw - 2, by + bh - 1, bx + bw, by + bh);
        shapeRenderer.triangle(bx + bw, by + bh, bx + bw, by + 1, bx + bw - 2, by + 2);
        shapeRenderer.triangle(bx + 2, by + bh - 2, bx + bw - 2, by + bh - 2, bx + bw - 1, by + bh);
        shapeRenderer.triangle(bx + bw - 1, by + bh, bx + 1, by + bh, bx + 2, by + bh - 2);
    }

    private void drawText(GlGraphics glGraphics) {
        int tx = x;
        switch (align) {
            case RIGHT:
                tx += w - font.size;
                break;
            case CENTER:
                tx += w / 2;
                break;
            case LEFT:
                tx += font.size;
        }
        font.draw(glGraphics.batch, text, tx + textOffsetX, y + (int) Math.ceil(0.5f * (h - 8 - font.size)), align);
    }
}
