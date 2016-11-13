package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.competitions.Competition;
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

public class SelectFolder extends GLScreen {

    private FileHandle fileHandle;
    private boolean isDataRoot;
    private Competition competition;

    public SelectFolder(GLGame game, FileHandle fileHandle, Competition competition) {
        super(game);
        this.fileHandle = fileHandle;
        this.competition = competition;
        isDataRoot = (fileHandle.path().equals(Assets.teamsFolder.path()));

        background = game.stateBackground;

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        // Folders buttons
        List<Widget> list = new ArrayList<Widget>();
        ArrayList<FileHandle> files = new ArrayList<FileHandle>(Arrays.asList(fileHandle.list()));
        Collections.sort(files, new Assets.CompareFileHandlesByName());
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                list.add(w);
                widgets.add(w);
            }
        }

        if (list.size() > 0) {
            Widget.arrange(game.gui.WIDTH, 350, 50, list);
            setSelectedWidget(list.get(0));
        }

        // Leagues buttons
        else {
            FileHandle leagueFile = fileHandle.child("LEAGUES.JSON");
            if (leagueFile.exists()) {
                League[] leagues = Assets.json.fromJson(League[].class, leagueFile.readString("UTF-8"));
                for (League league : leagues) {
                    w = new LeagueButton(league);
                    list.add(w);
                    widgets.add(w);
                }
                if (leagues.length > 0) {
                    Widget.arrange(game.gui.WIDTH, 350, 50, list);
                    setSelectedWidget(list.get(0));
                }
            }
        }

        w = new ExitButton();
        widgets.add(w);
        if (selectedWidget == null) {
            setSelectedWidget(w);
        }
    }

    class TitleBar extends Button {

        public TitleBar() {
            String title = "";
            switch (game.getState()) {
                case COMPETITION:
                case FRIENDLY:
                    int diff = competition.numberOfTeams - game.teamList.size();
                    title = Assets.strings.get((diff == 0) ? "CHANGE TEAMS FOR" : "CHOOSE TEAMS FOR")
                            + " " + competition.name;
                    if (!isDataRoot) {
                        title += " - " + fileHandle.name().replace('_', ' ');
                    }
                    break;

                case EDIT:
                    title = Assets.strings.get("EDIT TEAMS");
                    if (!isDataRoot) {
                        title += " - " + fileHandle.name().replace('_', ' ');
                    }
                    break;

                case TRAINING:
                    // TODO
                    break;
            }
            int w = Math.max(960, 80 + 16 * title.length());
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
            setSize(340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(fileHandle.name().replace('_', ' '), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case COMPETITION:
                case FRIENDLY:
                    competition.absolutePath = fileHandle.path();
                    break;

                default:
                    break;
            }
            game.setScreen(new SelectFolder(game, fileHandle, competition));
        }
    }

    class LeagueButton extends Button {

        League league;

        public LeagueButton(League league) {
            this.league = league;
            setSize(340, 40);
            setColors(0x1B4D85, 0x256AB7, 0x001D3E);
            setText(league.name, Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            switch (game.getState()) {
                case COMPETITION:
                case FRIENDLY:
                    game.setScreen(new SelectTeams(game, fileHandle, league, competition));
                    break;

                case EDIT:
                    game.setScreen(new SelectTeam(game, fileHandle, league));
                    break;

                case TRAINING:
                    // TODO
                    break;
            }
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
                game.setScreen(new SelectFolder(game, fileHandle.parent(), competition));
            }
        }
    }
}