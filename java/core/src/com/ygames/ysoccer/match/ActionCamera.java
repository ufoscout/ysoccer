package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PITCH_H;
import static com.ygames.ysoccer.match.Const.PITCH_W;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;

class ActionCamera {

    enum Mode {
        STILL,
        FOLLOW_BALL,
        REACH_TARGET
    }

    enum Speed {
        NORMAL,
        FAST,
        WARP
    }

    private Mode mode;
    private Speed speed;

    float x; // position
    float y;

    private float dx;
    private float dy;

    private float vx;
    private float vy;

    private float offsetX;
    private float offsetY;

    private boolean xLimited;
    private boolean yLimited;

    private Vector2 target;
    private float targetDistance;

    private Ball ball;
    private int screenWidth;
    private int screenHeight;
    private int zoom;

    ActionCamera(Ball ball) {
        this.ball = ball;
        speed = Speed.NORMAL;
        target = new Vector2();
    }

    Mode getMode() {
        return mode;
    }

    ActionCamera setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    Speed getSpeed() {
        return speed;
    }

    ActionCamera setSpeed(Speed speed) {
        this.speed = speed;
        return this;
    }

    ActionCamera setOffset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
        return this;
    }

    ActionCamera setLimited(boolean xLimited, boolean yLimited) {
        this.xLimited = xLimited;
        this.yLimited = yLimited;
        return this;
    }

    ActionCamera setTarget(float x, float y) {
        target.set(x, y);
        return this;
    }

    void setScreenParameters(int screenWidth, int screenHeight, int zoom) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.zoom = zoom;
        dx = CENTER_X - screenWidth / (2 * zoom / 100.0f);
        dy = CENTER_Y - screenHeight / (2 * zoom / 100.0f);
    }

    public float getTargetDistance() {
        return targetDistance;
    }

    void update() {

        switch (mode) {

            case STILL:
                // do nothing
                break;

            case FOLLOW_BALL:
                // "remember" last direction of movement
                if (ball.v * EMath.cos(ball.a) != 0) {
                    offsetX = screenWidth / 20.0f * EMath.cos(ball.a);
                }
                if (ball.v * EMath.sin(ball.a) != 0) {
                    offsetY = screenHeight / 20.0f * EMath.sin(ball.a);
                }

                // speed
                if (Math.abs(vx) > Math.abs(ball.v * EMath.cos(ball.a))) {
                    // decelerate
                    vx = (1 - 5.0f / SECOND) * vx;
                } else {
                    // accelerate
                    vx = vx + (2.5f / SECOND) * Math.signum(offsetX) * Math.abs(vx - ball.v * EMath.cos(ball.a));
                }
                if (Math.abs(vy) > Math.abs(ball.v * EMath.sin(ball.a))) {
                    // decelerate
                    vy = (1 - 5.0f / SECOND) * vy;
                } else {
                    // accelerate
                    vy = vy + (2.5f / SECOND) * Math.signum(offsetY) * Math.abs(vy - ball.v * EMath.sin(ball.a));
                }

                x = x + vx / SECOND;
                y = y + vy / SECOND;

                // near the point "ball+offset"
                if (Math.abs(ball.x + offsetX - (x - dx)) >= 10) {
                    float f = ball.x + offsetX - (x - dx);
                    x = x + (10.0f / SECOND) * (1 + speed.ordinal()) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                if (Math.abs(ball.y + offsetY - (y - dy)) >= 10) {
                    float f = ball.y + offsetY - (y - dy);
                    y = y + (10.0f / SECOND) * (1 + speed.ordinal()) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                break;

            case REACH_TARGET:
                float x0 = target.x - (x - dx);
                float y0 = target.y - (y - dy);
                float a = EMath.aTan2(y0, x0);
                targetDistance = EMath.sqrt(Math.abs(x0) + Math.abs(y0));
                x += (10.0f / SECOND) * (1 + speed.ordinal()) * targetDistance * EMath.cos(a);
                y += (10.0f / SECOND) * (1 + speed.ordinal()) * targetDistance * EMath.sin(a);
                break;
        }

        // keep inside pitch
        float xMin = 0;
        float xMax = PITCH_W - screenWidth / (zoom / 100.0f);
        float yMin = 0;
        float yMax = PITCH_H - screenHeight / (zoom / 100.0f);

        if (xLimited && (screenWidth / (zoom / 100.0f) < 1600)) {
            xMin = CENTER_X - TOUCH_LINE - screenWidth / (16 * zoom / 100.0f);
            xMax = CENTER_X + TOUCH_LINE + screenWidth / (16 * zoom / 100.0f) - screenWidth / (zoom / 100.0f);
        }
        if (yLimited) {
            yMin = CENTER_Y - GOAL_LINE - screenHeight / (4 * zoom / 100.0f);
            yMax = CENTER_Y + GOAL_LINE + screenHeight / (4 * zoom / 100.0f) - screenHeight / (zoom / 100.0f);
        }

        if (x < xMin) {
            x = xMin;
        }

        if (x > xMax) {
            x = xMax;
        }

        if (y < yMin) {
            y = yMin;
        }

        if (y > yMax) {
            y = yMax;
        }
    }
}
