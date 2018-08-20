package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;

public class Tournament extends Competition implements Json.Serializable {

    public ArrayList<Round> rounds;
    public AwayGoals awayGoals;

    public Tournament() {
        super(Type.TOURNAMENT);
        rounds = new ArrayList<Round>();
        awayGoals = AwayGoals.OFF;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);

        Round[] roundsArray = json.readValue("rounds", Round[].class, jsonData);
        if (roundsArray != null) {
            for (Round round : roundsArray) {
                round.setTournament(this);
                rounds.add(round);
            }
        }
        if (hasTwoLegsRound()) {
            awayGoals = json.readValue("awayGoals", AwayGoals.class, AwayGoals.AFTER_90_MINUTES, jsonData);
        }
    }

    @Override
    public void write(Json json) {
        super.write(json);

        json.writeValue("rounds", rounds, Round[].class, Round.class);
        if (hasTwoLegsRound()) {
            json.writeValue("awayGoals", awayGoals);
        }
    }

    @Override
    public void start(ArrayList<Team> teams) {
        super.start(teams);

        if (!getRound().isPreset()) {
        }
    }

    public Round getRound() {
        return rounds.get(currentRound);
    }

    public void addKnockout(int numberOfTeams, int numberOfLegs, boolean seeded, Round.ExtraTime extraTime) {
        Knockout knockout = new Knockout();
        knockout.numberOfTeams = numberOfTeams;
        knockout.numberOfLegs = numberOfLegs;
        knockout.seeded = seeded;
        knockout.extraTime = extraTime;
        rounds.add(knockout);
        updateRoundNames();
    }

    public void addGroups(int numberOfTeams, boolean seeded) {
        Groups groups = new Groups();
        groups.numberOfTeams = numberOfTeams;
        groups.seeded = seeded;
        rounds.add(groups);
        updateRoundNames();
    }

    private void updateRoundNames() {
        for (int i = 0; i < rounds.size(); i++) {
            if(Knockout.class.isInstance(rounds.get(i))) {
                rounds.get(i).name = getKnockoutLabel(i);
            } else {
                rounds.get(i).name = getGroupsLabel(i);
            }
        }
    }

    private String getKnockoutLabel(int round) {
        if (round == rounds.size() - 1) {
            return "FINAL";
        } else if (round == rounds.size() - 2) {
            return "SEMI-FINAL";
        } else if (round == rounds.size() - 3) {
            return "QUARTER-FINAL";
        } else if (round == 0) {
            return "FIRST ROUND";
        } else if (round == 1) {
            return "SECOND ROUND";
        } else {
            return "THIRD ROUND";
        }
    }

    private String getGroupsLabel(int round) {
        if (round == rounds.size() - 1) {
            return "FINAL ROUND";
        } else if (round == 0) {
            return "FIRST ROUND";
        } else if (round == 1) {
            return "SECOND ROUND";
        } else if (round == 2) {
            return "THIRD ROUND";
        } else if (round == 3) {
            return "FOURTH ROUND";
        } else {
            return "FIFTH ROUND";
        }
    }

    @Override
    public Match getMatch() {
        return null;
    }

    @Override
    public boolean isEnded() {
        return currentRound == rounds.size() - 1; // TODO && currentRound.isEnded()
    }

    public boolean hasTwoLegsRound() {
        for (Round round : rounds) {
            if (Knockout.class.isInstance(round)) {
                if (((Knockout) round).numberOfLegs == 2) {
                    return true;
                }
            }
        }
        return false;
    }
}
