package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_REPLAY;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;

abstract class MatchState extends SceneState {

    boolean displayControlledPlayer;
    boolean displayBallOwner;
    boolean displayGoalScorer;
    boolean displayTime;
    boolean displayWindVane;
    boolean displayRosters;
    boolean displayScore;
    boolean displayPenaltiesScore;
    boolean displayStatistics;
    boolean displayRadar;
    boolean displayBenchPlayers;
    boolean displayBenchFormation;
    boolean displayTacticsSwitch;

    boolean checkReplayKey = true;
    boolean checkPauseKey = true;

    Match match;

    MatchState(MatchFsm matchFsm) {
        super(matchFsm);

        this.match = matchFsm.getMatch();
    }

    MatchFsm getFsm() {
        return (MatchFsm) fsm;
    }

    SceneFsm.Action[] checkCommonConditions() {

        if (checkPauseKey && Gdx.input.isKeyPressed(P)) {
            return newAction(HOLD_FOREGROUND, STATE_PAUSE);
        }

        if (checkReplayKey && Gdx.input.isKeyPressed(R)) {
            return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
        }

        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitMatch();
            return null;
        }

        return null;
    }

    void quitMatch() {
        match.quit();
    }
}
