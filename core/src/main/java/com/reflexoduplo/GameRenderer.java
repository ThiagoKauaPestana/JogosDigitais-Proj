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
    private BitmapFont         fontGrande;
    private BitmapFont         fontMedia;
    private BitmapFont         fontPequena;
    private GlyphLayout        layout;

    private static final float W = GameWorld.WORLD_WIDTH;
    private static final float H = GameWorld.WORLD_HEIGHT;

    // Cores de fundo dinâmicas para cada Fase (Estilo Neon Escuro)
    private static final Color COR_FUNDO_FASE1 = new Color(0.04f, 0.04f, 0.12f, 1f); // Azul escuro
    private static final Color COR_FUNDO_FASE2 = new Color(0.08f, 0.03f, 0.12f, 1f); // Roxo escuro
    private static final Color COR_FUNDO_FASE3 = new Color(0.14f, 0.03f, 0.03f, 1f); // Vermelho escuro
    private static final Color COR_FUNDO_MAX   = new Color(0.02f, 0.10f, 0.06f, 1f); // Verde Cyber escuro

    private static final Color CHAO_COR     = new Color(0.10f, 0.10f, 0.28f, 1f);
    private static final Color LINHA_GUIA   = new Color(1.0f,  1.0f,  1.0f,  0.15f);
    private static final Color AMARELO      = new Color(1f,    0.85f, 0f,    1f);
    private static final Color CIANO        = new Color(0f,    0.9f,  1f,    1f);
    private static final Color VERDE        = new Color(0.1f,  0.85f, 0.25f, 1f);

    public GameRenderer(GameWorld world, SpriteBatch batch) {
        this.world = world;
        this.batch = batch;

        camera   = new OrthographicCamera();
        viewport = new FitViewport(W, H, camera);
        viewport.apply();
        camera.position.set(W / 2f, H / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();
        layout        = new GlyphLayout();

        // Inicializa as fontes aplicando filtro para não pixelar
        fontGrande = new BitmapFont();
        fontGrande.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
        fontGrande.getData().setScale(3.2f);

        fontMedia = new BitmapFont();
        fontMedia.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
        fontMedia.getData().setScale(2.0f);

        fontPequena = new BitmapFont();
        fontPequena.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
        fontPequena.getData().setScale(1.3f);
    }

    public void render(float delta) {
        // Seleciona o fundo dinâmico de acordo com a fase do GameWorld
        Color corFundoAtual;
        switch (world.getFase()) {
            case 2:  corFundoAtual = COR_FUNDO_FASE2; break;
            case 3:  corFundoAtual = COR_FUNDO_FASE3; break;
            case 4:  corFundoAtual = COR_FUNDO_MAX;   break;
            default: corFundoAtual = COR_FUNDO_FASE1; break;
        }

        Gdx.gl.glClearColor(corFundoAtual.r, corFundoAtual.g, corFundoAtual.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // 1. Cenário de Fundo (Chão e Teto perfeitamente alinhados)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderCenarioFundo();
        shapeRenderer.end();

        // 2. Obstáculos e Player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Obstacle obs : world.getObstacleManager().getObstaculos()) {
            obs.render(shapeRenderer);
        }
        world.getPlayer().render(shapeRenderer);
        shapeRenderer.end();

        // 3. Interface Visual (HUD) dependendo do Estado
        if (world.getEstado() == GameWorld.EstadoJogo.MENU) {
            renderMenu();
        } else if (world.getEstado() == GameWorld.EstadoJogo.RODANDO) {
            renderJogo();
        } else if (world.getEstado() == GameWorld.EstadoJogo.PERDEU) {
            renderPerdeu();
        }
    }

    private void renderCenarioFundo() {
        shapeRenderer.setColor(CHAO_COR);
        // Chão inferior
        shapeRenderer.rect(0, 0, W, GameWorld.ALTURA_LINHA_BAIXO);
        
        // Ajustado: O teto visual agora começa colado no topo do limite do Player e do Espinho
        float inicioTetoVisual = GameWorld.ALTURA_LINHA_CIMA + Player.HEIGHT;
        shapeRenderer.rect(0, inicioTetoVisual, W, H - inicioTetoVisual);

        // Linhas de movimento pontilhadas
        shapeRenderer.setColor(LINHA_GUIA);
        float sX = world.getScrollX() % 80f;
        for (float x = -80f; x < W + 80f; x += 80f) {
            shapeRenderer.rect(x - sX, GameWorld.ALTURA_LINHA_BAIXO + 6, 40, 4);
            shapeRenderer.rect(x - sX, inicioTetoVisual - 10, 40, 4);
        }
    }

    private void renderJogo() {
        // Painel Superior Esquerdo (Pontos e Fase) - Compactado em h: 95
        renderPainelHUD(10, H - 105, 260, 95);
        batch.begin();
        fontMedia.setColor(Color.WHITE);
        fontMedia.draw(batch, "PONTOS", 22f, H - 16f);
        fontGrande.setColor(AMARELO);
        fontGrande.draw(batch, String.valueOf(world.getPontuacao()), 22f, H - 36f);
        
        fontMedia.setColor(Color.WHITE);
        String txtFase = (world.getFase() >= 4) ? "FASE: MAX" : "FASE: " + world.getFase();
        fontMedia.draw(batch, txtFase, 22f, H - 74f);
        batch.end();

        // Painel Superior Direito (Recorde)
        renderPainelHUD(W - 270, H - 75, 260, 65);
        batch.begin();
        fontMedia.setColor(Color.WHITE);
        layout.setText(fontMedia, "RECORDE");
        fontMedia.draw(batch, "RECORDE", W - 260f + (260f - layout.width) / 2f, H - 16f);
        fontGrande.setColor(CIANO);
        layout.setText(fontGrande, String.valueOf(world.getRecorde()));
        fontGrande.draw(batch, String.valueOf(world.getRecorde()), W - 260f + (260f - layout.width) / 2f, H - 36f);
        batch.end();

        // Painel Central (Linha Ativa)
        String linhaStr = (world.getPlayer().getLinhaAtual() == Player.Linha.BAIXO) ? "LINHA: BAIXO" : "LINHA: CIMA";
        Color corLinha = (world.getPlayer().getLinhaAtual() == Player.Linha.BAIXO) ? CIANO : AMARELO;
        renderPainelHUD(W / 2f - 140, H - 55, 280, 45);
        batch.begin();
        fontMedia.setColor(corLinha);
        layout.setText(fontMedia, linhaStr);
        fontMedia.draw(batch, linhaStr, W / 2f - layout.width / 2f, H - 16f);
        batch.end();

        renderBarraVelocidade();
    }

    private void renderBarraVelocidade() {
        float pct = (world.getVelocidadeAtual() - GameWorld.VELOCIDADE_INICIAL) / (GameWorld.VELOCIDADE_MAXIMA - GameWorld.VELOCIDADE_INICIAL);
        float bw = 220f;
        float bx = W / 2f - bw / 2f;
        float by = H - 78f;

        renderPainelHUD(bx - 6, by - 4, bw + 12, 14);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.25f, 1f);
        shapeRenderer.rect(bx, by, bw, 6);
        shapeRenderer.setColor(Color.WHITE.cpy().lerp(VERDE, pct));
        shapeRenderer.rect(bx, by, bw * pct, 6);
        shapeRenderer.end();
    }

    private void renderMenu() {
        renderOverlay(new Color(0.02f, 0.02f, 0.08f, 0.85f));
        float cx = W / 2f;
        float cy = H / 2f;

        batch.begin();
        fontGrande.setColor(Color.WHITE);
        layout.setText(fontGrande, "REFLEXO DUPLO");
        fontGrande.draw(batch, "REFLEXO DUPLO", cx - layout.width / 2f, cy + 140f);

        fontMedia.setColor(AMARELO);
        String sub = "Treino de Atenção Alternada";
        layout.setText(fontMedia, sub);
        fontMedia.draw(batch, sub, cx - layout.width / 2f, cy + 80f);

        float pulso = (float) Math.abs(Math.sin(world.getTempoJogo() * 4f));
        renderBotaoInstrucao(batch, cx, cy - 40f, "CLIQUE", "Para iniciar o desafio", VERDE, pulso);
        batch.end();
    }


    private void renderPerdeu() {
        renderOverlay(new Color(0.12f, 0.02f, 0.02f, 0.82f));
        float cx = W / 2f;
        float cy = H / 2f;

        batch.begin();
        fontGrande.setColor(new Color(1f, 0.2f, 0.2f, 1f));
        layout.setText(fontGrande, "FIM DE JOGO");
        fontGrande.draw(batch, "FIM DE JOGO", cx - layout.width / 2f, cy + 140f);

        renderQuadroResultado(batch, cx - 220, cy - 20, 440, 100);

        float pulso = world.podeReiniciar() ? (float) Math.abs(Math.sin(world.getTempoJogo() * 4.5f)) : 0f;
        if (world.podeReiniciar()) {
            renderBotaoInstrucao(batch, cx, cy - 120f, "CLIQUE", "Jogar novamente", VERDE, pulso);
        }
        batch.end();
    }

    private void renderQuadroResultado(SpriteBatch b, float x, float y, float w, float h) {
        b.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.end();
        b.begin();

        fontMedia.setColor(Color.WHITE);
        fontMedia.draw(b, "Pontos:", x + 25, y + 65);
        fontGrande.setColor(AMARELO);
        fontGrande.draw(b, String.valueOf(world.getPontuacao()), x + 210, y + 82);
    }

    private void renderBotaoInstrucao(SpriteBatch b, float cx, float cy, String txtBtn, String txtInst, Color corBtn, float p) {
        b.end();
        float bw = 210f + (p * 12f);
        float bh = 64f + (p * 4f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(corBtn.r, corBtn.g, corBtn.b, 0.9f);
        shapeRenderer.rect(cx - bw / 2f, cy - bh / 2f, bw, bh);
        shapeRenderer.end();
        b.begin();

        fontMedia.setColor(Color.BLACK);
        layout.setText(fontMedia, txtBtn);
        fontMedia.draw(b, txtBtn, cx - layout.width / 2f, cy + layout.height / 2f);

        fontMedia.setColor(Color.WHITE);
        fontMedia.draw(b, txtInst, cx + (bw / 2f) + 25f, cy + 12f);
    }

    private void renderOverlay(Color cor) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(cor);
        shapeRenderer.rect(0, 0, W, H);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderPainelHUD(float x, float y, float w, float h) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.05f, 0.05f, 0.18f, 0.75f);
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}