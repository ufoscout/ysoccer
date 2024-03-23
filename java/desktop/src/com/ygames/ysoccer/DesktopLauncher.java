package com.ygames.ysoccer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 720);
        config.setWindowIcon(
                "images/icon_128.png",
                "images/icon_32.png"
        );
        config.setForegroundFPS(60);
        config.setTitle("YSoccer");
        new Lwjgl3Application(new YSoccer(), config);
    }
}
