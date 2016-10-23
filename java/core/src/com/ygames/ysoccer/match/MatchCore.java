package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGame;

import java.util.ArrayList;
import java.util.List;

public class MatchCore {

    private GlGame game;

    private MatchFsm fsm;

    final Ball ball;
    public final Team team[];
    public int benchSide; // 1 = home upside, -1 = home downside

    public MatchSettings settings;

    final List<Goal> goals;

    public MatchCore(GlGame game, Team[] team, MatchSettings matchSettings) {
        this.game = game;
        this.team = team;
        this.settings = matchSettings;

        fsm = new MatchFsm(this);

        ball = new Ball(this);

        goals = new ArrayList<Goal>();
    }
}
