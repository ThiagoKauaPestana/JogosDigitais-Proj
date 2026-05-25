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

    private static final Color FUNDO        = new Color(0.04f, 0.04f, 0.12f, 1f);
    private static final Color CHAO_COR     = new Color(0.10f, 0.10f, 0.28f, 1f);
    private static final Color LINHA_GUIA   = new Color(0.5f,  0.5f,  1.0f,  0.4f);
    private static final Color GRADE        = new Color(1f,    1f,    1f,    0.03f);
    private static final Color ZONA_ATIVA   = new Color(0.18f, 0.18f, 0.45f, 0.2f);
    private static final Color OVERLAY_MENU = new Color(0f,    0f,    0.06f, 0.82f);
    private static final Color AMARELO      = new Color(1f,    0.92f, 0f,    1f);
    private static final Color CIANO        = new Color(0f,    0.88f, 1f,    1f);
    private static final Color VERDE        = new Color(0.2f,  1f,    0.4f,  1f);
    private static final Color VERMELHO     = new Color(1f,    0.15f, 0.1f,  1f);

    private static final float ALTURA_CHAO = 72f;
    private static final float MARG_ZONA   = 10f;

    private float tempoPulso = 0f;

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

        fontGrande = new BitmapFont();
        fontGrande.getData().setScale(3.2f);

        fontMedia = new BitmapFont();
        fontMedia.getData().setScale(2.0f);

        fontPequena = new BitmapFont();
        fontPequena.getData().setScale(1.3f);
    }

    public void render(float delta) {
        tempoPulso += delta;

        Gdx.gl.glClearColor(FUNDO.r, FUNDO.g, FUNDO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        switch (world.getEstado()) {
            case MENU:    renderCenario(); renderMenuInicial();              break;
            case RODANDO: renderCenario(); renderJogo();                     break;
            case PERDEU:  renderCenario(); renderJogo(); renderGameOver();   break;
        }
    }

    private void renderCenario() {
        renderFundo();
        renderZonaLinhaAtiva();
        renderChao();
        renderLinhasGuia();
        renderGrade();
        renderObstaculos();
        renderPlayer();
    }

    private void renderFundo() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(FUNDO);
        shapeRenderer.rect(0, 0, W, H);
        shapeRenderer.end();
    }

    private void renderZonaLinhaAtiva() {
        Player p = world.getPlayer();
        float yBase = (p.getLinhaAtual() == Player.Linha.BAIXO)
            ? p.getYLinhaBaixo() : p.getYLinhaCima();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ZONA_ATIVA);
        shapeRenderer.rect(0, yBase - MARG_ZONA, W, Player.HEIGHT + MARG_ZONA * 2);
        shapeRenderer.end();
    }

    private void renderChao() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(CHAO_COR);
        shapeRenderer.rect(0, 0, W, ALTURA_CHAO);
        shapeRenderer.rect(0, H - ALTURA_CHAO, W, ALTURA_CHAO);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.55f, 0.55f, 1f, 0.9f);
        shapeRenderer.line(0, ALTURA_CHAO, W, ALTURA_CHAO);
        shapeRenderer.line(0, H - ALTURA_CHAO, W, H - ALTURA_CHAO);
        shapeRenderer.end();
    }

    private void renderLinhasGuia() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(LINHA_GUIA);
        shapeRenderer.line(0, GameWorld.ALTURA_LINHA_BAIXO + Player.HEIGHT / 2f,
                           W, GameWorld.ALTURA_LINHA_BAIXO + Player.HEIGHT / 2f);
        shapeRenderer.line(0, GameWorld.ALTURA_LINHA_CIMA + Player.HEIGHT / 2f,
                           W, GameWorld.ALTURA_LINHA_CIMA + Player.HEIGHT / 2f);
        shapeRenderer.end();
    }

    private void renderGrade() {
        float scroll = world.getScrollX() % 90f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(GRADE);
        for (float x = -scroll; x < W; x += 90f)
            shapeRenderer.line(x, ALTURA_CHAO, x, H - ALTURA_CHAO);
        for (float y = ALTURA_CHAO; y < H - ALTURA_CHAO; y += 90f)
            shapeRenderer.line(0, y, W, y);
        shapeRenderer.end();
    }

    private void renderObstaculos() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Obstacle obs : world.getObstacleManager().getObstaculos())
            obs.render(shapeRenderer);
        shapeRenderer.end();
    }

    private void renderPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        world.getPlayer().render(shapeRenderer);
        shapeRenderer.end();
    }

    private void renderJogo() {
        renderPainelHUD(10, H - 80, 280, 70);
        batch.begin();
        fontMedia.setColor(Color.WHITE);
        fontMedia.draw(batch, "PONTOS", 26f, H - 20f);
        fontGrande.setColor(AMARELO);
        fontGrande.draw(batch, String.valueOf(world.getPontuacao()), 26f, H - 42f);
        batch.end();

        renderPainelHUD(W - 290, H - 80, 280, 70);
        batch.begin();
        fontMedia.setColor(Color.WHITE);
        layout.setText(fontMedia, "RECORDE");
        fontMedia.draw(batch, "RECORDE", W - 280f + (280f - layout.width) / 2f, H - 20f);
        fontGrande.setColor(CIANO);
        layout.setText(fontGrande, String.valueOf(world.getRecorde()));
        fontGrande.draw(batch, String.valueOf(world.getRecorde()),
            W - 280f + (280f - layout.width) / 2f, H - 42f);
        batch.end();

        String linhaStr = (world.getPlayer().getLinhaAtual() == Player.Linha.BAIXO)
            ? "LINHA: BAIXO" : "LINHA: CIMA";
        Color corLinha = (world.getPlayer().getLinhaAtual() == Player.Linha.BAIXO)
            ? CIANO : AMARELO;
        renderPainelHUD(W / 2f - 160, H - 68, 320, 58);
        batch.begin();
        fontMedia.setColor(corLinha);
        layout.setText(fontMedia, linhaStr);
        fontMedia.draw(batch, linhaStr, W / 2f - layout.width / 2f, H - 18f);
        batch.end();

        renderBarraVelocidade();
    }

    private void renderBarraVelocidade() {
        float progresso = (world.getVelocidadeAtual() - GameWorld.VELOCIDADE_INICIAL)
                        / (GameWorld.VELOCIDADE_MAXIMA  - GameWorld.VELOCIDADE_INICIAL);
        float barW = 320f;
        float barH = 14f;
        float barX = W / 2f - barW / 2f;
        float barY = 18f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.4f, 1f);
        shapeRenderer.rect(barX, barY, barW, barH);

        Color corBarra = new Color(
            Math.min(1f, progresso * 2f),
            Math.max(0f, 1f - progresso),
            0f, 1f
        );
        shapeRenderer.setColor(corBarra);
        shapeRenderer.rect(barX, barY, barW * progresso, barH);
        shapeRenderer.end();

        batch.begin();
        fontPequena.setColor(Color.WHITE);
        layout.setText(fontPequena, "VELOCIDADE");
        fontPequena.draw(batch, "VELOCIDADE", W / 2f - layout.width / 2f, 48f);
        batch.end();
    }

    private void renderMenuInicial() {
        renderOverlay(OVERLAY_MENU);

        float pulso = 0.7f + 0.3f * (float) Math.sin(tempoPulso * 2.2f);
        float cx = W / 2f;
        float cy = H / 2f;

        batch.begin();

        fontGrande.getData().setScale(5.0f);
        fontGrande.setColor(CIANO);
        layout.setText(fontGrande, "REFLEXO DUPLO");
        fontGrande.draw(batch, "REFLEXO DUPLO", cx - layout.width / 2f, cy + 190f);
        fontGrande.getData().setScale(3.2f);

        fontMedia.setColor(new Color(0.7f, 0.7f, 1f, 1f));
        layout.setText(fontMedia, "Jogo de reabilitacao e reflexo");
        fontMedia.draw(batch, "Jogo de reabilitacao e reflexo",
            cx - layout.width / 2f, cy + 120f);

        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.6f, 0.8f);
        shapeRenderer.rect(cx - 260f, cy + 78f, 520f, 2f);
        shapeRenderer.end();
        batch.begin();

        renderBotaoInstrucao(batch, cx, cy + 35f,
            "CLIQUE", "Trocar linha / Iniciar", VERDE, pulso);
        renderBotaoInstrucao(batch, cx, cy - 45f,
            "SEGURAR", "Acao extra", AMARELO, 1f);

        fontPequena.setColor(0.5f, 0.5f, 0.7f, 1f);
        String dica = "F11 = Tela cheia   ESC = Sair";
        layout.setText(fontPequena, dica);
        fontPequena.draw(batch, dica, cx - layout.width / 2f, cy - 130f);

        batch.end();
    }

    private void renderBotaoInstrucao(SpriteBatch b, float cx, float cy,
                                       String label, String acao,
                                       Color cor, float pulso) {
        float bw = 500f, bh = 54f;
        b.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(cor.r * 0.15f, cor.g * 0.15f, cor.b * 0.15f, 0.9f);
        shapeRenderer.rect(cx - bw / 2f, cy - bh / 2f, bw, bh);
        shapeRenderer.setColor(cor.r, cor.g, cor.b, 0.25f * pulso);
        shapeRenderer.rect(cx - bw / 2f, cy - bh / 2f, bw, bh);
        shapeRenderer.end();
        b.begin();

        fontMedia.setColor(cor);
        layout.setText(fontMedia, label);
        fontMedia.draw(b, label, cx - layout.width / 2f - 80f, cy + 12f);

        fontPequena.setColor(Color.WHITE);
        layout.setText(fontPequena, acao);
        fontPequena.draw(b, acao, cx - layout.width / 2f + 50f, cy - 4f);
    }

    private void renderGameOver() {
        renderOverlay(new Color(0f, 0f, 0f, 0.72f));

        float pulso  = 0.75f + 0.25f * (float) Math.sin(tempoPulso * 2.5f);
        float cx     = W / 2f;
        float cy     = H / 2f;
        boolean pode = world.podeReiniciar();

        batch.begin();

        fontGrande.getData().setScale(5.5f);
        fontGrande.setColor(VERMELHO);
        layout.setText(fontGrande, "PERDEU!");
        fontGrande.draw(batch, "PERDEU!", cx - layout.width / 2f, cy + 195f);
        fontGrande.getData().setScale(3.2f);

        fontGrande.setColor(Color.WHITE);
        layout.setText(fontGrande, "Pontuacao: " + world.getPontuacao());
        fontGrande.draw(batch, "Pontuacao: " + world.getPontuacao(),
            cx - layout.width / 2f, cy + 115f);

        boolean novoRecorde = world.getPontuacao() >= world.getRecorde()
                           && world.getPontuacao() > 0;
        if (novoRecorde) {
            fontMedia.setColor(AMARELO);
            layout.setText(fontMedia, "NOVO RECORDE!");
            fontMedia.draw(batch, "NOVO RECORDE!", cx - layout.width / 2f, cy + 60f);
        } else {
            fontMedia.setColor(new Color(0.6f, 0.6f, 0.9f, 1f));
            layout.setText(fontMedia, "Recorde: " + world.getRecorde());
            fontMedia.draw(batch, "Recorde: " + world.getRecorde(),
                cx - layout.width / 2f, cy + 60f);
        }

        if (pode) {
            renderBotaoInstrucao(batch, cx, cy - 20f,
                "CLIQUE", "Jogar novamente", VERDE, pulso);

            fontPequena.setColor(0.5f, 0.5f, 0.7f, 1f);
            String msg = "Clique para jogar novamente";
            layout.setText(fontPequena, msg);
            fontPequena.draw(batch, msg, cx - layout.width / 2f, cy - 110f);
        }

        batch.end();
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
        shapeRenderer.setColor(0.05f, 0.05f, 0.18f, 0.85f);
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(W / 2f, H / 2f, 0);
        camera.update();
    }

    public void dispose() {
        shapeRenderer.dispose();
        fontGrande.dispose();
        fontMedia.dispose();
        fontPequena.dispose();
    }
}