package com.ygames.ysoccer.screens;

import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

public class Friendly extends GlScreen {
    public Friendly(GlGame game, FileHandle fileHandle) {
        super(game);
        background = new Image("images/backgrounds/menu_friendly.jpg");

        Widget w;
        w = new TitleBar();
        widgets.add(w);

        FileHandle[] files = fileHandle.list();
        int i = 0;
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                w = new FolderButton(file);
                w.setPosition((game.settings.GUI_WIDTH - 340) / 2, 300 + 60 * i);
                widgets.add(w);
                if (i == 0) {
                    selectedWidget = w;
                }
                i++;
            }
        }
    }

    class TitleBar extends Button {
        public TitleBar() {
            setGeometry((game.settings.GUI_WIDTH - 400) / 2, 30, 400, 40);
            setColors(0x2D855D, 0x3DB37D, 0x1E5027);
            setText(Assets.strings.get("FRIENDLY"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    class FolderButton extends Button {

        FileHandle fileHandle;

        public FolderButton(FileHandle fileHandle) {
            this.fileHandle = fileHandle;
            setSize(340, 40);
            setColors(0x568200, 0x77B400, 0x243E00);
            setText(fileHandle.name().toUpperCase(), Font.Align.CENTER, Assets.font14);
        }
    }
}