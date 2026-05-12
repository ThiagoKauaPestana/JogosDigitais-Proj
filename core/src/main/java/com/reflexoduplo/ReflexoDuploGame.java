package com.reflexoduplo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * ReflexoDuploGame — Classe principal do jogo.
 */
public class ReflexoDuploGame extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}