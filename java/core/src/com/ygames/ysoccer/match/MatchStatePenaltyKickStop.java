package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStatePenaltyKickStop extends MatchState {

    private boolean allPlayersReachingTarget;
    private boolean move;
    private Player penaltyKicker;

    MatchStatePenaltyKickStop(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_PENALTY_KICK_STOP;

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        // set the player targets relative to penalty
        // even before moving the ball itself
        match.ball.updateZone(0, Math.signum(match.foul.position.y) * Const.PENALTY_SPOT_Y, 0, 0);
        match.updateTeamTactics();
        match.team[HOME].lineup.get(0).setTarget(0, match.team[HOME].side * (Const.GOAL_LINE - 8));
        match.team[AWAY].lineup.get(0).setTarget(0, match.team[AWAY].side * (Const.GOAL_LINE - 8));
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 1; i < TEAM_SIZE; i++) {
                Player player = match.team[t].lineup.get(i);
                player.tx = player.tx >= 0 ? Math.max(player.tx, 100) : -Math.max(-player.tx, 100);
                player.ty = Math.signum(player.ty) * Math.min(Math.abs(player.ty), Const.GOAL_LINE - Const.PENALTY_AREA_H - 4);
            }
        }

        penaltyKicker = match.foul.opponent.team.lastOfLineup();
        penaltyKicker.setTarget(-40 * match.ball.ySide, Math.signum(match.foul.position.y) * (Const.PENALTY_SPOT_Y - 45));

        match.resetAutomaticInputDevices();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        allPlayersReachingTarget = true;
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = match.team[t].lineup.get(i);

                // wait for tackle and down states to finish
                if (player.checkState(PlayerFsm.STATE_TACKLE) || player.checkState(PlayerFsm.STATE_DOWN)) {
                    allPlayersReachingTarget = false;
                } else {
                    player.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
                }
            }
        }

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();
            match.ball.collisionGoal();
            match.ball.collisionJumpers();
            match.ball.collisionNetOut();
            match.ball.collisionNet();

            move = match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, match.foul.position.x);
            matchRenderer.updateCameraY(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (allPlayersReachingTarget && !move) {
            match.ball.setPosition(0, Math.signum(match.foul.position.y) * Const.PENALTY_SPOT_Y, 0);
            match.ball.updatePrediction();

            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_PENALTY_KICK);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }

        InputDevice inputDevice;
        for (int t = HOME; t <= AWAY; t++) {
            inputDevice = match.team[t].fire2Down();
            if (inputDevice != null) {
                fsm.benchStatus.team = match.team[t];
                fsm.benchStatus.inputDevice = inputDevice;
                fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_BENCH_ENTER);
                return;
            }
        }
    }
}
