package com.reflexoduplo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameRenderer {

    private final GameWorld world;

    private OrthographicCamera camera;
    private Viewport           viewport;
    private ShapeRenderer      shapeRenderer;
    private SpriteBatch        batch;
    private BitmapFont         font;
    private GlyphLayout        layout;

    private static final Color COR_FUNDO      = new Color(0.05f, 0.05f, 0.15f, 1f);
    private static final Color COR_CHAO       = new Color(0.12f, 0.12f, 0.30f, 1f);
    private static final Color COR_LINHA_GUIA = new Color(0.5f,  0.5f,  1f,   0.35f);
    private static final Color COR_GRADE      = new Color(1f,    1f,    1f,   0.04f);
    private static final Color COR_ZONA_ATIVA = new Color(0.2f,  0.2f,  0.5f, 0.15f);
    private static final Color COR_OVERLAY    = new Color(0f,    0f,    0f,   0.65f);

    private static final float ALTURA_CHAO = 55f;
    private static final float MARG_ZONA   = 8f;

    public GameRenderer(GameWorld world, SpriteBatch batch) {
        this.world = world;
        this.batch = batch;

        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(GameWorld.WORLD_WIDTH / 2f, GameWorld.WORLD_HEIGHT / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();
        font   = new BitmapFont();
        layout = new GlyphLayout();
        font.getData().setScale(1.4f);
        font.setColor(Color.WHITE);
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(COR_FUNDO.r, COR_FUNDO.g, COR_FUNDO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        renderFundo();
        renderZonaLinhaAtiva();
        renderChao();
        renderLinhasGuia();
        renderGrade();
        renderObstaculos();
        renderPlayer();
        renderHUD();

        if (world.getEstado() == GameWorld.EstadoJogo.PERDEU) {
            renderTelaGameOver();
        }
    }

    private void renderFundo() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COR_FUNDO);
        shapeRenderer.rect(0, 0, GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT);
        shapeRenderer.end();
    }

    private void renderZonaLinhaAtiva() {
        Player p = world.getPlayer();
        float yBase = (p.getLinhaAtual() == Player.Linha.BAIXO)
            ? p.getYLinhaBaixo()
            : p.getYLinhaCima();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COR_ZONA_ATIVA);
        shapeRenderer.rect(0, yBase - MARG_ZONA,
                           GameWorld.WORLD_WIDTH, Player.HEIGHT + MARG_ZONA * 2);
        shapeRenderer.end();
    }

    private void renderChao() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COR_CHAO);
        shapeRenderer.rect(0, 0, GameWorld.WORLD_WIDTH, ALTURA_CHAO);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 1f, 0.8f);
        shapeRenderer.line(0, ALTURA_CHAO, GameWorld.WORLD_WIDTH, ALTURA_CHAO);
        shapeRenderer.line(0, GameWorld.WORLD_HEIGHT - ALTURA_CHAO,
                           GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT - ALTURA_CHAO);
        shapeRenderer.end();
    }

    private void renderLinhasGuia() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COR_LINHA_GUIA);

        float yBaixo = GameWorld.ALTURA_LINHA_BAIXO + Player.HEIGHT / 2f;
        shapeRenderer.line(0, yBaixo, GameWorld.WORLD_WIDTH, yBaixo);

        float yCima = GameWorld.ALTURA_LINHA_CIMA + Player.HEIGHT / 2f;
        shapeRenderer.line(0, yCima, GameWorld.WORLD_WIDTH, yCima);

        shapeRenderer.end();
    }

    private void renderGrade() {
        float scroll = world.getScrollX() % 80f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COR_GRADE);
        for (float x = -scroll; x < GameWorld.WORLD_WIDTH; x += 80f) {
            shapeRenderer.line(x, ALTURA_CHAO, x, GameWorld.WORLD_HEIGHT - ALTURA_CHAO);
        }
        for (float y = ALTURA_CHAO; y < GameWorld.WORLD_HEIGHT - ALTURA_CHAO; y += 80f) {
            shapeRenderer.line(0, y, GameWorld.WORLD_WIDTH, y);
        }
        shapeRenderer.end();
    }

    private void renderObstaculos() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Obstacle obs : world.getObstacleManager().getObstaculos()) {
            obs.render(shapeRenderer);
        }
        shapeRenderer.end();
    }

    private void renderPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        world.getPlayer().render(shapeRenderer);
        shapeRenderer.end();
    }

    private void renderHUD() {
        String linhaStr = (world.getPlayer().getLinhaAtual() == Player.Linha.BAIXO)
            ? "LINHA: BAIXO" : "LINHA: CIMA";

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "REFLEXO DUPLO", 14f, GameWorld.WORLD_HEIGHT - 10f);
        font.draw(batch, "PONTOS: " + world.getPontuacao(), 14f, GameWorld.WORLD_HEIGHT - 28f);

        layout.setText(font, linhaStr);
        font.draw(batch, linhaStr,
                  GameWorld.WORLD_WIDTH - layout.width - 14f,
                  GameWorld.WORLD_HEIGHT - 10f);

        font.getData().setScale(0.9f);
        font.setColor(0.55f, 0.55f, 0.8f, 1f);
        font.draw(batch, "[SPACE / W] Trocar linha    [R] Reiniciar", 14f, 28f);
        font.getData().setScale(1.4f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void renderTelaGameOver() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COR_OVERLAY);
        shapeRenderer.rect(0, 0, GameWorld.WORLD_WIDTH, GameWorld.WORLD_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        float cx = GameWorld.WORLD_WIDTH  / 2f;
        float cy = GameWorld.WORLD_HEIGHT / 2f;

        batch.begin();
        font.getData().setScale(2.4f);
        font.setColor(Color.RED);
        layout.setText(font, "PERDEU!");
        font.draw(batch, "PERDEU!", cx - layout.width / 2f, cy + 55f);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        layout.setText(font, "Pontuacao: " + world.getPontuacao());
        font.draw(batch, "Pontuacao: " + world.getPontuacao(),
                  cx - layout.width / 2f, cy + 10f);

        if (world.podeReiniciar()) {
            font.getData().setScale(1.2f);
            font.setColor(0.7f, 1f, 0.7f, 1f);
            String msg = "Pressione R para jogar novamente";
            layout.setText(font, msg);
            font.draw(batch, msg, cx - layout.width / 2f, cy - 30f);
        }

        font.getData().setScale(1.4f);
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