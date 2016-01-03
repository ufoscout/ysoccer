package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class DiyCompetition extends GlScreen {

    public DiyCompetition(GlGame game) {
        super(game);

        background = new Image("images/backgrounds/menu_competition.jpg");

        Widget w;

        w = new TitleBar();
        widgets.add(w);

        w = new LeagueButton();
        widgets.add(w);
        selectedWidget = w;

        w = new CupButton();
        widgets.add(w);

        w = new TournamentButton();
        widgets.add(w);
    }

    class TitleBar extends Button {

        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 300) / 2, 30, 300, 40);
            setColors(0x415600, 0x5E7D00, 0x243000);
            setText(Assets.strings.get("DIY COMPETITION"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class LeagueButton extends Button {

        public LeagueButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 300, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("LEAGUE"), Font.Align.CENTER, Assets.font14);
        }
    }

    class CupButton extends Button {

        public CupButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 380, 340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(Assets.strings.get("CUP"), Font.Align.CENTER, Assets.font14);
        }
    }

    class TournamentButton extends Button {

        public TournamentButton() {
            setGeometry((game.settings.GUI_WIDTH - 340) / 2, 460, 340, 40);
            setColors(0x666666, 0x8F8D8D, 0x404040);
            setText(Assets.strings.get("TOURNAMENT"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }
}
