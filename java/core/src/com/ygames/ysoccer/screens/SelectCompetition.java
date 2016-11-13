package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.Cup;
import com.ygames.ysoccer.competitions.League;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelectCompetition extends GLScreen {

    private FileHandle fileHandle;
    private boolean isDataRoot;

    public SelectCompetition(GLGame game, FileHandle fileHandle) {
        super(game);
        this.fileHandle = fileHandle;
        isDataRoot = (fileHandle.path().equals(Assets.competitionsFolder.path()));

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        // Competitions buttons
        List<Widget> competitionsList = new ArrayList<Widget>();

        FileHandle leaguesFile = fileHandle.child("leagues.json");
        if (leaguesFile.exists()) {
            League[] leagues = Assets.json.fromJson(League[].class, leaguesFile.readString("UTF-8"));
            for (League league : leagues) {
                league.init();
                league.category = Competition.Category.PRESET_COMPETITION;
                w = new CompetitionButton(league);
                competitionsList.add(w);
                widgets.add(w);
            }
        }

        FileHandle cupsFile = fileHandle.child("cups.json");
        if (cupsFile.exists()) {
            Cup[] cups = Assets.json.fromJson(Cup[].class, cupsFile.readString("UTF-8"));
            for (Cup cup : cups) {
                cup.init();
                cup.category = Competition.Category.PRESET_COMPETITION;
                w = new CompetitionButton(cup);
                competitionsList.add(w);
                widgets.add(w);
            }
        }

        // Folders buttons
        List<Widget> foldersList = new ArrayList<Widget>();
        ArrayList<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(fileHandle.list()));
        Collections.sort(files, new Assets.CompareFileHandlesByName());
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                foldersList.add(w);
                widgets.add(w);
            }
        }

        int topY = 365 - 30 * (competitionsList.size() + Widget.getRows(foldersList)) / 2;
        int centerY = topY + 30 * competitionsList.size() / 2;
        if (competitionsList.size() > 0) {
            Widget.arrange(game.gui.WIDTH, centerY, 30, competitionsList);
            setSelectedWidget(competitionsList.get(0));
        }
        centerY += 30 * (competitionsList.size() + Widget.getRows(foldersList)) / 2 + 6;
        if (foldersList.size() > 0) {
            Widget.arrange(game.gui.WIDTH, centerY, 30, foldersList);
            setSelectedWidget(foldersList.get(0));
        }

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            setSelectedWidget(w);
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            String title = Assets.strings.get("CHOOSE PRESET COMPETITION");
            if (!isDataRoot) {
                title += " - " + fileHandle.name();
            }
            int w = Math.max(400, 80 + 16 * title.length());
            setGeometry((game.gui.WIDTH - w) / 2, 30, w, 40);
            setColors(game.stateColor);
            setText(title, Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class FolderButton extends Button {

        FileHandle fileHandle;

        public FolderButton(FileHandle fileHandle) {
            this.fileHandle = fileHandle;
            setSize(340, 30);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(fileHandle.name(), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new SelectCompetition(game, fileHandle));
        }
    }

    class CompetitionButton extends Button {

        Competition competition;

        public CompetitionButton(Competition competition) {
            this.competition = competition;
            setSize(480, 30);
            setColors(0x1B4D85, 0x256AB7, 0x001D3E);
            setText(competition.name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.teamList = competition.loadTeams();
            game.setScreen(new AllSelectedTeams(game, fileHandle, competition));
        }
    }

    class ExitButton extends Button {

        public ExitButton() {
            if (isDataRoot) {
                setColors(0xC8000E, 0xFF1929, 0x74040C);
                setText(Assets.strings.get("ABORT"), Font.Align.CENTER, Assets.font14);
            } else {
                setColors(0xC84200, 0xFF6519, 0x803300);
                setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
            }
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
        }

        @Override
        public void onFire1Down() {
            if (isDataRoot) {
                game.setScreen(new Main(game));
            } else {
                game.setScreen(new SelectCompetition(game, fileHandle.parent()));
            }
        }
    }
}