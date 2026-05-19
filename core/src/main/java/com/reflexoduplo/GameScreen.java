package com.reflexoduplo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

/**
 * GameScreen — Semana 4 (versão final)
 *
 * - Lê os botões físicos via ArduinoInput (Serial USB)
 * - Fallback automático para teclado se Arduino não estiver conectado
 * - Ambos funcionam simultaneamente (durante apresentação)
 * - Passa status do Arduino para o GameRenderer exibir na tela
 */
public class GameScreen implements Screen {

    private final ReflexoDuploGame game;
    private GameWorld    world;
    private GameRenderer renderer;
    private ArduinoInput arduino;

    // Fallback de teclado: evita disparo repetido por frame
    private boolean teclado1Anterior = false;
    private boolean teclado2Anterior = false;

    public GameScreen(ReflexoDuploGame game) {
        this.game = game;
        world    = new GameWorld();
        renderer = new GameRenderer(world, game.batch);

        // Tenta conectar ao Arduino — falha silenciosa, cai no teclado
        arduino  = new ArduinoInput();
        renderer.setArduinoConectado(arduino.isConectado());
    }

    @Override
    public void render(float delta) {
        // 1. Lê a serial do Arduino (non-blocking)
        arduino.poll();

        // 2. Processa input (Arduino + teclado)
        handleInput();

        // 3. Atualiza lógica
        world.update(delta);

        // 4. Renderiza
        renderer.render(delta);
    }

    private void handleInput() {
        // ===== BOTÃO 1 — Arduino OU teclado =====
        boolean b1Arduino = arduino.isBotao1Pressionado();

        boolean teclado1Atual = Gdx.input.isKeyPressed(Input.Keys.SPACE)
                             || Gdx.input.isKeyPressed(Input.Keys.W)
                             || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean b1Teclado = teclado1Atual && !teclado1Anterior;
        teclado1Anterior  = teclado1Atual;

        if (b1Arduino || b1Teclado) {
            world.acaoBotao1();
        }

        // ===== BOTÃO 2 — Arduino OU teclado =====
        boolean b2Arduino = arduino.isBotao2Pressionado();

        boolean teclado2Atual = Gdx.input.isKeyPressed(Input.Keys.D)
                             || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean b2Teclado = teclado2Atual && !teclado2Anterior;
        teclado2Anterior  = teclado2Atual;

        if (b2Arduino || b2Teclado) {
            world.acaoBotao2();
        }

        // ===== RETRY pelo teclado (quando no estado PERDEU) =====
        if (world.getEstado() == GameWorld.EstadoJogo.PERDEU
                && world.podeReiniciar()
                && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            world.reiniciar();
        }

        // ===== TELA CHEIA (F11 ou ALT+ENTER) =====
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleTelaCheia();
        }

        // ===== SAIR =====
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void toggleTelaCheia() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1280, 720);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    @Override
    public void resize(int w, int h) {
        renderer.resize(w, h);
    }

    @Override public void show()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        renderer.dispose();
        arduino.dispose();
    }
}