package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GlColor2;
import com.ygames.ysoccer.framework.GlColor3;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.RgbPair;
import com.ygames.ysoccer.math.Emath;
import com.ygames.ysoccer.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public enum Role {GOALKEEPER, RIGHT_BACK, LEFT_BACK, DEFENDER, RIGHT_WINGER, LEFT_WINGER, MIDFIELDER, ATTACKER}

    public enum Skill {PASSING, SHOOTING, HEADING, TACKLING, CONTROL, SPEED, FINISHING}

    public static final String[] roleLabels = {
            "ROLES.GOALKEEPER",
            "ROLES.RIGHT_BACK",
            "ROLES.LEFT_BACK",
            "ROLES.DEFENDER",
            "ROLES.RIGHT_WINGER",
            "ROLES.LEFT_WINGER",
            "ROLES.MIDFIELDER",
            "ROLES.ATTACKER"
    };

    public static final String[] skillLabels = {
            "SKILLS.PASSING",
            "SKILLS.SHOOTING",
            "SKILLS.HEADING",
            "SKILLS.TACKLING",
            "SKILLS.CONTROL",
            "SKILLS.SPEED",
            "SKILLS.FINISHING"
    };

    public String name;
    public String shirtName;
    Team team;
    public String nationality;
    int index;
    public Role role;
    public String number;

    public String hairColor;
    public String hairStyle;
    public String skinColor;

    public Skills skills;

    int skillKeeper;

    public int value; // 0 to 49

    public int goals;

    public InputDevice inputDevice;
    Ai ai;

    float kickAngle;

    Player facingPlayer;
    float facingAngle;
    MatchCore match;
    PlayerFsm fsm;

    float speed;

    boolean isVisible;

    public float x;
    public float y;
    float z;
    float x0;
    float y0;
    float z0;
    float v;
    float vz;
    public float a;
    float thrustX; // horiz.speed in keeper saves (min=0, max=1)
    float thrustZ; // vert.speed in keeper saves (min=0, max=1)

    float tx; // x position (target)
    float ty; // y position (target)

    float fmx; // 0..7 direction
    float fmy; // 1 = standing, 0 and 2 = running
    float fmySweep;

    float ballDistance;

    // from 0 to BALL_PREDICTION-1: frames required to reach the ball
    // equal to BALL_PREDICTION: ball too far to be reached
    // should be updated every frame
    int frameDistance;

    void animationStandRun() {
        fmx = Math.round(((a + 360) % 360) / 45) % 8;
        if (v > 0) {
            fmySweep = (fmySweep + 0.16f * v / 1000) % 4;
            if (fmySweep > 3) {
                fmy = fmySweep - 2;
            } else {
                fmy = fmySweep;
            }
        } else {
            fmy = 1;
        }
    }

    void animationScorer() {
        fmx = Math.round(((a + 360) % 360) / 45) % 8;
        if (v > 0) {
            fmySweep = (fmySweep + 0.16f * v / 1000) % 4;
            if (fmySweep > 3) {
                fmy = 12;
            } else {
                fmy = 11 + fmySweep;
            }
        } else {
            fmy = 1;
        }
    }

    public void getPossession() {
        if ((ballDistance <= 8)
                && Emath.dist(x0, y0, match.ball.x0, match.ball.y0) > 8
                && (match.ball.z < (Const.PLAYER_H + Const.BALL_R))) {

            float smoothedBallV = match.ball.v * 0.5f;
            Vector2 ballVec = new Vector2(smoothedBallV, match.ball.a, true);
            Vector2 playerVec = new Vector2(v, a, true);

            Vector2 differenceVec = playerVec.sub(ballVec);

            if (differenceVec.v < 220 + 7 * skills.control) {
                match.ball.setOwner(this);
                match.ball.x = x + (Const.BALL_R - 1) * Emath.cos(a);
                match.ball.y = y + (Const.BALL_R - 1) * Emath.sin(a);
                match.ball.v = v;
                match.ball.a = a;
            } else {
                match.ball.setOwner(this);
                match.ball.setOwner(null);
                match.ball.collisionPlayer(this, 0.5f * differenceVec.v);
            }

            match.ball.vz = match.ball.vz / (2 + skills.control);
        }
    }

    float targetDistance() {
        return Emath.dist(tx, ty, x, y);
    }

    float targetAngle() {
        return Emath.aTan2(ty - y, tx - x);
    }

    void watchBall() {
        a = Math.round((Emath.aTan2(y - match.ball.y, x - match.ball.x) + 180) / 45.0f) * 45.0f;
    }

    public String getRoleLabel() {
        return roleLabels[role.ordinal()];
    }

    // sets and orders skills in function of the role of the player
    public Skill[] getOrderedSkills() {

        if (role == Role.GOALKEEPER) {
            return null;
        }

        Skill[] orderedSkills = new Skill[7];

        // set starting order
        int[] skill = new int[7];
        if ((role == Role.RIGHT_BACK) || (role == Role.LEFT_BACK)) {
            skill[0] = skills.tackling;
            skill[1] = skills.speed;
            skill[2] = skills.passing;
            skill[3] = skills.shooting;
            skill[4] = skills.heading;
            skill[5] = skills.control;
            skill[6] = skills.finishing;
            orderedSkills[0] = Skill.TACKLING;
            orderedSkills[1] = Skill.SPEED;
            orderedSkills[2] = Skill.PASSING;
            orderedSkills[3] = Skill.SHOOTING;
            orderedSkills[4] = Skill.HEADING;
            orderedSkills[5] = Skill.CONTROL;
            orderedSkills[6] = Skill.FINISHING;

        } else if (role == Role.DEFENDER) {
            skill[0] = skills.tackling;
            skill[1] = skills.heading;
            skill[2] = skills.passing;
            skill[3] = skills.speed;
            skill[4] = skills.shooting;
            skill[5] = skills.control;
            skill[6] = skills.finishing;
            orderedSkills[0] = Skill.TACKLING;
            orderedSkills[1] = Skill.HEADING;
            orderedSkills[2] = Skill.PASSING;
            orderedSkills[3] = Skill.SPEED;
            orderedSkills[4] = Skill.SHOOTING;
            orderedSkills[5] = Skill.CONTROL;
            orderedSkills[6] = Skill.FINISHING;

        } else if ((role == Role.RIGHT_WINGER) || (role == Role.LEFT_WINGER)) {
            skill[0] = skills.control;
            skill[1] = skills.speed;
            skill[2] = skills.passing;
            skill[3] = skills.tackling;
            skill[4] = skills.heading;
            skill[5] = skills.finishing;
            skill[6] = skills.shooting;
            orderedSkills[0] = Skill.CONTROL;
            orderedSkills[1] = Skill.SPEED;
            orderedSkills[2] = Skill.PASSING;
            orderedSkills[3] = Skill.TACKLING;
            orderedSkills[4] = Skill.HEADING;
            orderedSkills[5] = Skill.FINISHING;
            orderedSkills[6] = Skill.SHOOTING;

        } else if (role == Role.MIDFIELDER) {
            skill[0] = skills.passing;
            skill[1] = skills.tackling;
            skill[2] = skills.control;
            skill[3] = skills.heading;
            skill[4] = skills.shooting;
            skill[5] = skills.speed;
            skill[6] = skills.finishing;
            orderedSkills[0] = Skill.PASSING;
            orderedSkills[1] = Skill.TACKLING;
            orderedSkills[2] = Skill.CONTROL;
            orderedSkills[3] = Skill.HEADING;
            orderedSkills[4] = Skill.SHOOTING;
            orderedSkills[5] = Skill.SPEED;
            orderedSkills[6] = Skill.FINISHING;

        } else if (role == Role.ATTACKER) {
            skill[0] = skills.heading;
            skill[1] = skills.finishing;
            skill[2] = skills.speed;
            skill[3] = skills.shooting;
            skill[4] = skills.control;
            skill[5] = skills.passing;
            skill[6] = skills.tackling;
            orderedSkills[0] = Skill.HEADING;
            orderedSkills[1] = Skill.FINISHING;
            orderedSkills[2] = Skill.SPEED;
            orderedSkills[3] = Skill.SHOOTING;
            orderedSkills[4] = Skill.CONTROL;
            orderedSkills[5] = Skill.PASSING;
            orderedSkills[6] = Skill.TACKLING;
        }

        boolean ordered = false;
        while (!ordered) {
            ordered = true;
            for (int i = 0; i < 6; i++) {
                if (skill[i] < skill[i + 1]) {
                    int tmp = skill[i];
                    skill[i] = skill[i + 1];
                    skill[i + 1] = tmp;

                    Skill s = orderedSkills[i];
                    orderedSkills[i] = orderedSkills[i + 1];
                    orderedSkills[i + 1] = s;

                    ordered = false;
                }
            }
        }

        return orderedSkills;
    }

    public int getDefenseRating() {
        switch (role) {
            case DEFENDER:
            case RIGHT_BACK:
            case LEFT_BACK:
            case MIDFIELDER:
                return skills.tackling
                        + skills.heading
                        + skills.passing
                        + skills.speed
                        + skills.control;
            default:
                return 0;
        }
    }

    public int getOffenseRating() {
        switch (role) {
            case RIGHT_WINGER:
            case LEFT_WINGER:
            case MIDFIELDER:
            case ATTACKER:
                return skills.heading
                        + skills.finishing
                        + skills.speed
                        + skills.shooting
                        + skills.control;
            default:
                return 0;
        }
    }

    public void copyFrom(Player player) {
        name = player.name;
        shirtName = player.shirtName;
        nationality = player.nationality;
        role = player.role;

        hairColor = player.hairColor;
        hairStyle = player.hairStyle;
        skinColor = player.skinColor;

        skills.passing = player.skills.passing;
        skills.shooting = player.skills.shooting;
        skills.heading = player.skills.heading;
        skills.tackling = player.skills.tackling;
        skills.control = player.skills.control;
        skills.speed = player.skills.speed;
        skills.finishing = player.skills.finishing;
    }

    public int getSkillValue(Skill skill) {
        int value = 0;
        switch (skill) {
            case PASSING:
                value = skills.passing;
                break;
            case SHOOTING:
                value = skills.shooting;
                break;
            case HEADING:
                value = skills.heading;
                break;
            case TACKLING:
                value = skills.tackling;
                break;
            case CONTROL:
                value = skills.control;
                break;
            case SPEED:
                value = skills.speed;
                break;
            case FINISHING:
                value = skills.finishing;
                break;
        }
        return value;
    }

    public void setSkillValue(Skill skill, int value) {
        switch (skill) {
            case PASSING:
                skills.passing = value;
                break;
            case SHOOTING:
                skills.shooting = value;
                break;
            case HEADING:
                skills.heading = value;
                break;
            case TACKLING:
                skills.tackling = value;
                break;
            case CONTROL:
                skills.control = value;
                break;
            case SPEED:
                skills.speed = value;
                break;
            case FINISHING:
                skills.finishing = value;
                break;
        }
    }

    public int getValue() {
        if (role == Role.GOALKEEPER) {
            return value;
        } else {
            return skills.passing
                    + skills.shooting
                    + skills.heading
                    + skills.tackling
                    + skills.control
                    + skills.speed
                    + skills.finishing;
        }
    }

    public String getPrice(double maxPlayerPrice) {
        if (getValue() == 49) {
            return Assets.moneyFormat(maxPlayerPrice) + "+";
        } else {
            return Assets.moneyFormat(maxPlayerPrice / (float) Math.pow(1.2, 48 - getValue()));
        }
    }

    public static String getSkillLabel(Skill skill) {
        return skillLabels[skill.ordinal()];
    }

    public static class Skills {
        int passing;
        int shooting;
        int heading;
        int tackling;
        int control;
        int speed;
        int finishing;
    }

    public Image createFace() {

        List<RgbPair> rgbPairs = new ArrayList<RgbPair>();

        // skin color                                                                              ;
        GlColor3 sc = Assets.getSkinColorByName(skinColor);
        rgbPairs.add(new RgbPair(0xFFFF6300, sc.color1));
        rgbPairs.add(new RgbPair(0xFFB54200, sc.color2));
        rgbPairs.add(new RgbPair(0xFF631800, sc.color3));

        // hair color
        if (hairStyle.equals("SHAVED")) {
            GlColor2 shavedColor = Assets.getShavedColor(skinColor, hairColor);
            if (shavedColor != null) {
                rgbPairs.add(new RgbPair(0xFF907130, shavedColor.color1));
                rgbPairs.add(new RgbPair(0xFF715930, shavedColor.color2));
            } else {
                rgbPairs.add(new RgbPair(0xFF907130, sc.color1));
                rgbPairs.add(new RgbPair(0xFF715930, sc.color2));
            }
        } else {
            GlColor3 hc = Assets.getHairColorByName(hairColor);
            rgbPairs.add(new RgbPair(0xFF907130, hc.color1));
            rgbPairs.add(new RgbPair(0xFF715930, hc.color2));
            rgbPairs.add(new RgbPair(0xFF514030, hc.color3));
        }

        String filename = "images/player/menu/" + hairStyle + ".PNG";
        return Image.loadImage(filename, rgbPairs);
    }

    boolean searchFacingPlayer(boolean longRange) {
        return searchFacingPlayer(longRange, false);
    }

    boolean searchFacingPlayer(boolean longRange, boolean inAction) {

        float minDistance = 0.0f;
        float maxDistance = Const.TOUCH_LINE / 2;
        if (longRange) {
            minDistance = Const.TOUCH_LINE / 2;
            maxDistance = Const.TOUCH_LINE;
        }

        float maxAngle;
        if (inAction) {
            maxAngle = 15.5f + skills.passing;
        } else {
            maxAngle = 22.5f;
        }

        float facingDelta = maxDistance * Emath.sin(maxAngle);

        facingPlayer = null;
        facingAngle = 0.0f;

        int len = team.lineup.size();
        for (int i = 0; i < len; i++) {
            Player ply = team.lineup.get(i);
            if (ply == this) {
                continue;
            }

            float plyDistance = Emath.dist(x, y, ply.x, ply.y);
            float plyAngle = ((Emath.aTan2(ply.y + 5 * Emath.sin(ply.a) - y,
                    ply.x + 5 * Emath.cos(ply.a) - x) - a + 540.0f) % 360.0f) - 180.0f;
            float plyDelta = plyDistance * Emath.sin(plyAngle);

            if (Math.abs(plyAngle) < maxAngle && plyDistance > minDistance
                    && plyDistance < maxDistance
                    && Math.abs(plyDelta) < Math.abs(facingDelta)) {
                facingPlayer = ply;
                facingAngle = plyAngle;
                facingDelta = plyDelta;
            }
        }

        return (facingPlayer != null);
    }
}
