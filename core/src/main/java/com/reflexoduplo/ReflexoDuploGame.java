package com.reflexoduplo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.reflexoduplo.screens.GameScreen;

public class ReflexoDuploGame extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Inicia direto na tela do jogo (Semana 1 - tela inicial simples)
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
