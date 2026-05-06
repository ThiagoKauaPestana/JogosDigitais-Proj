package com.reflexoduplo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.reflexoduplo.ReflexoDuploGame;
import com.reflexoduplo.world.GameRenderer;
import com.reflexoduplo.world.GameWorld;


public class GameScreen implements Screen {

    private final ReflexoDuploGame game;
    private GameWorld    world;
    private GameRenderer renderer;

    // Controle de input (evita múltiplos disparos por toque)
    private boolean botao1Pressionado = false;
    private boolean botao2Pressionado = false;

    public GameScreen(ReflexoDuploGame game) {
        this.game = game;
        world    = new GameWorld();
        renderer = new GameRenderer(world, game.batch);
    }


    @Override
    public void render(float delta) {
        // 1. Captura input
        handleInput();

        // 2. Atualiza lógica
        world.update(delta);

        // 3. Renderiza
        renderer.render(delta);
    }


    private void handleInput() {
        // ----- Botão 1: trocar linha (SPACE ou W ou toque lado esquerdo) -----
        boolean b1Atual = Gdx.input.isKeyPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyPressed(Input.Keys.W)
            || Gdx.input.isKeyPressed(Input.Keys.UP)
            || isToqueLadoEsquerdo();

        if (b1Atual && !botao1Pressionado) {
            world.acaoBotao1();
        }
        botao1Pressionado = b1Atual;

        // ----- Botão 2: variação da mecânica (D ou RIGHT ou toque lado direito) -----
        boolean b2Atual = Gdx.input.isKeyPressed(Input.Keys.D)
            || Gdx.input.isKeyPressed(Input.Keys.RIGHT)
            || isToqueLadoDireito();

        if (b2Atual && !botao2Pressionado) {
            world.acaoBotao2();
        }
        botao2Pressionado = b2Atual;

        // ----- Reiniciar (R) -----
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            world.reiniciar();
        }

        // ----- Sair (ESC) - substituir por menu na Semana 4 -----
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    /** Verifica se há toque na metade esquerda da tela. */
    private boolean isToqueLadoEsquerdo() {
        if (!Gdx.input.isTouched()) return false;
        return Gdx.input.getX() < Gdx.graphics.getWidth() / 2f;
    }

    /** Verifica se há toque na metade direita da tela. */
    private boolean isToqueLadoDireito() {
        if (!Gdx.input.isTouched()) return false;
        return Gdx.input.getX() >= Gdx.graphics.getWidth() / 2f;
    }


    @Override
    public void resize(int width, int height) {s
        renderer.resize(width, height);
    }

    @Override public void show()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
