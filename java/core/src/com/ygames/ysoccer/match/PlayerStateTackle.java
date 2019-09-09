package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;

class PlayerStateTackle extends PlayerState {

    private boolean hit;

    PlayerStateTackle(PlayerFsm fsm) {
        super(STATE_TACKLE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        hit = false;

        player.v = 1.4f * (player.speed) * (1 + 0.02f * player.skills.tackling);
        player.x += 8 * Emath.cos(player.a);
        player.y += 8 * Emath.sin(player.a);
    }

    @Override
    void doActions() {
        super.doActions();

        if (!hit) {
            if ((ball.z < 5) && (player.ballDistance < 9)) {
                float angle = Emath.aTan2(ball.y - player.y, ball.x - player.x);
                float angleDiff = Math.abs((((angle - player.a + 540) % 360)) - 180);
                if ((angleDiff <= 90)
                        && (player.ballDistance * Emath.sin(angleDiff) <= (8 + 0.3f * player.skills.tackling))) {

                    ball.setOwner(player);
                    ball.v = player.v * (1 + 0.02f * player.skills.tackling);
                    hit = true;

                    if ((player.inputDevice.value)
                            && (Math.abs((((player.a - player.inputDevice.angle + 540) % 360)) - 180) < 67.5)) {
                        ball.a = player.inputDevice.angle;
                    } else {
                        ball.a = player.a;
                    }

                    Assets.Sounds.kick.play(0.1f * (1 + 0.03f * timer) * Assets.Sounds.volume / 100f);
                }
            }
        }

        player.v -= (20 + player.matchSettings.grass.friction) / Const.SECOND * Math.sqrt(Math.abs(player.v));

        // animation
        player.fmx = Math.round((((player.a + 360) % 360)) / 45) % 8;
        player.fmy = 4;
    }

    @Override
    State checkConditions() {
        if (player.v < 30) {
            return fsm.stateStandRun;
        }
        return null;
    }
}
