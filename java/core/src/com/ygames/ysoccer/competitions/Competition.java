package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.match.Pitch;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Time;

import java.util.Calendar;

public class Competition {
    public String name;
    public Team[] teams;
    public boolean bySeason; // true = by season, false = by pitch type
    public int seasonStart;
    public int seasonEnd;
    public int pitchType;
    public int time;

    public Competition() {
        bySeason = true;
        seasonStart = Calendar.AUGUST;
        seasonEnd = Calendar.MAY;
        pitchType = Pitch.RANDOM;
        time = Time.DAY;
    }
}
