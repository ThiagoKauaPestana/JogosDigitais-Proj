package com.reflexoduplo.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.reflexoduplo.entities.Player;

public class GameRenderer {

    private final GameWorld world;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    // Cores do cenário
    private static final Color COR_FUNDO_CIMA  = new Color(0.05f, 0.05f, 0.15f, 1f);
    private static final Color COR_FUNDO_BAIXO = new Color(0.08f, 0.08f, 0.25f, 1f);
    private static final Color COR_CHAO        = new Color(0.15f, 0.15f, 0.35f, 1f);
    private static final Color COR_LINHA_GUIA  = new Color(0.4f, 0.4f, 0.8f, 0.4f);
    private static final Color COR_GRADE       = new Color(1f, 1f, 1f, 0.04f);

    // Tamanho da faixa do chão
    private static final float ALTURA_CHAO = 55f;

    public GameRenderer(GameWorld world, SpriteBatch batch) {
        this.world = world;
        this.batch = batch;

        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(GameWorld.WORLD_WIDTH / 2f, GameWorld.WORLD_HEIGHT / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(); // fonte padrão do libGDX (Semana 4: fonte maior para idosos)
        font.setColor(Color.WHITE);
    }

    /** Chamado a cada frame pelo GameScreen. */
    public void render(float delta) {
        // Limpa a tela
        com.badlogic.gdx.Gdx.gl.glClearColor(
            COR_FUNDO_CIMA.r, COR_FUNDO_CIMA.g, COR_FUNDO_CIMA.b, 1f
        );
        com.badlogic.gdx.Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        renderFundo();
        renderChao();
        renderLinhasGuia();
        renderGrade();
        renderPlayer();
        renderHUD();
    }

    private void renderFundo() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fundo superior
        shapeRenderer.setColor(COR_FUNDO_CIMA);
        shapeRenderer.rect(0, ALTURA_CHAO, GameWorld.WORLD_WIDTH,
            GameWorld.WORLD_HEIGHT - ALTURA_CHAO);

        // Fundo inferior (área do chão)
        shapeRenderer.setColor(COR_FUNDO_BAIXO);
        shapeRenderer.rect(0, 0, GameWorld.WORLD_WIDTH, ALTURA_CHAO);

        shapeRenderer.end();
    }

    private void renderChao() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COR_CHAO);
        shapeRenderer.rect(0, 0, GameWorld.WORLD_WIDTH, ALTURA_CHAO);
        shapeRenderer.end();

        // Borda superior do chão
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 1f, 0.8f);
        shapeRenderer.line(0, ALTURA_CHAO, GameWorld.WORLD_WIDTH, ALTURA_CHAO);
        shapeRenderer.end();
    }

    /** Linhas horizontais que indicam onde o personagem pode correr (Semana 1: visual). */
    private void renderLinhasGuia() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COR_LINHA_GUIA);

        // Linha de baixo
        float yBaixo = GameWorld.ALTURA_LINHA_BAIXO + Player.HEIGHT / 2f;
        shapeRenderer.line(0, yBaixo, GameWorld.WORLD_WIDTH, yBaixo);

        // Linha de cima
        float yCima = GameWorld.ALTURA_LINHA_CIMA + Player.HEIGHT / 2f;
        shapeRenderer.line(0, yCima, GameWorld.WORLD_WIDTH, yCima);

        shapeRenderer.end();
    }

    /** Grade de fundo animada que reforça a sensação de velocidade. */
    private void renderGrade() {
        float scroll = world.getScrollX() % 80f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COR_GRADE);

        // Linhas verticais se movendo
        for (float x = -scroll; x < GameWorld.WORLD_WIDTH; x += 80f) {
            shapeRenderer.line(x, ALTURA_CHAO, x, GameWorld.WORLD_HEIGHT);
        }
        // Linhas horizontais estáticas
        for (float y = ALTURA_CHAO; y < GameWorld.WORLD_HEIGHT; y += 80f) {
            shapeRenderer.line(0, y, GameWorld.WORLD_WIDTH, y);
        }

        shapeRenderer.end();
    }

    private void renderPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        world.getPlayer().render(shapeRenderer);
        shapeRenderer.end();
    }

    /** HUD simples: título e instrução para Semana 1. */
    private void renderHUD() {
        batch.begin();
        font.draw(batch, "REFLEXO DUPLO", 16f, GameWorld.WORLD_HEIGHT - 12f);
        font.draw(batch, "Semana 1 - Base do jogo", 16f, GameWorld.WORLD_HEIGHT - 28f);

        // Aviso de placeholder para o desenvolvedor
        font.setColor(0.6f, 0.6f, 0.6f, 1f);
        font.draw(batch, "[Semana 2: botoes de interacao]",
            GameWorld.WORLD_WIDTH / 2f - 120f, 30f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(GameWorld.WORLD_WIDTH / 2f, GameWorld.WORLD_HEIGHT / 2f, 0);
        camera.update();
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}
