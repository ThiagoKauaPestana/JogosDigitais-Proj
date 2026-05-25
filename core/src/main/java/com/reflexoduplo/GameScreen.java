package com.reflexoduplo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen {

    private final ReflexoDuploGame game;
    private GameWorld    world;
    private GameRenderer renderer;

    private boolean cliqueSeguradoAnterior = false;

    public GameScreen(ReflexoDuploGame game) {
        this.game = game;
        world    = new GameWorld();
        renderer = new GameRenderer(world, game.batch);
    }

    @Override
    public void render(float delta) {
        handleInput();
        world.update(delta);
        renderer.render(delta);
    }

    private void handleInput() {
        boolean clicouAgora = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        boolean segurando   = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        if (clicouAgora) {
            world.acaoBotao1();
        }

        if (segurando && !cliqueSeguradoAnterior) {
            world.acaoBotao2();
        }
        cliqueSeguradoAnterior = segurando;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(1280, 720);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override public void resize(int w, int h) { renderer.resize(w, h); }
    @Override public void show()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        renderer.dispose();
    }
}