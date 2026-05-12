package com.reflexoduplo.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.reflexoduplo.ReflexoDuploGame;

public class DesktopLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Reflexo Duplo - Semana 2");
        config.setWindowedMode(800, 480);
        config.setResizable(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new ReflexoDuploGame(), config);
    }
}