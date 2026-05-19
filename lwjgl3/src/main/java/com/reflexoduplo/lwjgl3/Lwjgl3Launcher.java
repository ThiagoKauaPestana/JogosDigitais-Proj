package com.reflexoduplo.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.reflexoduplo.ReflexoDuploGame;

/**
 * Lwjgl3Launcher — Ponto de entrada desktop (Semana 4 / Apresentação)
 *
 * - Inicia em TELA CHEIA automática
 * - Resolução base 1280x720 (compatível com a maioria dos projetores)
 * - F11 dentro do jogo alterna entre tela cheia e janela
 */
public class Lwjgl3Launcher {

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setTitle("Reflexo Duplo");
        config.setForegroundFPS(60);

        // ----- TELA CHEIA para apresentação -----
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        // Para desenvolvimento/debug, comente a linha acima e descomente a abaixo:
        // config.setWindowedMode(1280, 720);

        config.setResizable(false);    // evita redimensionamento acidental
        config.useVsync(true);

        new Lwjgl3Application(new ReflexoDuploGame(), config);
    }
}