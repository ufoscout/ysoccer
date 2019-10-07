package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;
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

    boolean checkReplayKey;

    // convenience references
    protected Match match;

    MatchState(MatchFsm.Id id, MatchFsm matchFsm) {
        super(id, matchFsm);

        this.match = matchFsm.getMatch();

        fsm.addState(this);

        checkReplayKey = true;
    }

    MatchFsm getFsm() {
        return (MatchFsm) fsm;
    }

    SceneFsm.Action[] checkCommonConditions() {

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
