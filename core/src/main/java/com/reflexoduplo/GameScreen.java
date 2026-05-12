package com.reflexoduplo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen {

    private final ReflexoDuploGame game;
    private GameWorld    world;
    private GameRenderer renderer;

    private boolean botao1Anterior = false;
    private boolean botao2Anterior = false;

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
        // Botão 1 — Trocar linha
        boolean b1 = Gdx.input.isKeyPressed(Input.Keys.SPACE)
                  || Gdx.input.isKeyPressed(Input.Keys.W)
                  || Gdx.input.isKeyPressed(Input.Keys.UP)
                  || isToqueLadoEsquerdo();

        if (b1 && !botao1Anterior) {
            world.acaoBotao1();
        }
        botao1Anterior = b1;

        // Botão 2 — Mecânica extra
        boolean b2 = Gdx.input.isKeyPressed(Input.Keys.D)
                  || Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                  || isToqueLadoDireito();

        if (b2 && !botao2Anterior) {
            world.acaoBotao2();
        }
        botao2Anterior = b2;

        // Reiniciar após derrota
        if (world.getEstado() == GameWorld.EstadoJogo.PERDEU
                && world.podeReiniciar()
                && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            world.reiniciar();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private boolean isToqueLadoEsquerdo() {
        if (!Gdx.input.isTouched()) return false;
        return Gdx.input.getX() < Gdx.graphics.getWidth() / 2f;
    }

    private boolean isToqueLadoDireito() {
        if (!Gdx.input.isTouched()) return false;
        return Gdx.input.getX() >= Gdx.graphics.getWidth() / 2f;
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