package com.reflexoduplo;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameWorld {

    public static final float WORLD_WIDTH  = 800f;
    public static final float WORLD_HEIGHT = 480f;

    public static final float ALTURA_LINHA_BAIXO = 60f;
    public static final float ALTURA_LINHA_CIMA  = WORLD_HEIGHT - 100f - Player.HEIGHT;

    public static final float VELOCIDADE_INICIAL = 280f;
    private float velocidadeAtual;
    private float scrollX;

    private Player          player;
    private ObstacleManager obstacleManager;

    public enum EstadoJogo { RODANDO, PERDEU }
    private EstadoJogo estado;

    private float pontuacao;

    // Delay após colisão antes de liberar o retry
    private float timerMorte = 0f;
    private static final float DELAY_MORTE = 0.4f;

    public GameWorld() {
        inicializar();
    }

    private void inicializar() {
        velocidadeAtual = VELOCIDADE_INICIAL;
        scrollX         = 0f;
        estado          = EstadoJogo.RODANDO;
        pontuacao       = 0f;
        timerMorte      = 0f;

        player = new Player(120f, ALTURA_LINHA_BAIXO, ALTURA_LINHA_CIMA);
        obstacleManager = new ObstacleManager(
            ALTURA_LINHA_BAIXO,
            ALTURA_LINHA_CIMA,
            WORLD_WIDTH
        );
    }

    public void update(float delta) {
        if (estado == EstadoJogo.PERDEU) {
            timerMorte -= delta;
            return;
        }

        scrollX    += velocidadeAtual * delta;
        pontuacao  += velocidadeAtual * delta * 0.1f;

        player.update(delta);
        obstacleManager.update(delta, velocidadeAtual);

        verificarColisoes();
    }

    private void verificarColisoes() {
        Rectangle boundsPlayer = player.getBounds();
        Array<Obstacle> obstaculos = obstacleManager.getObstaculos();

        for (Obstacle obs : obstaculos) {
            if (boundsPlayer.overlaps(obs.getBounds())) {
                player.acionarFlashColisao();
                estado     = EstadoJogo.PERDEU;
                timerMorte = DELAY_MORTE;
                return;
            }
        }
    }

    /** Botão 1 — Troca a linha do personagem. */
    public void acaoBotao1() {
        if (estado == EstadoJogo.RODANDO) {
            player.trocarLinha();
        }
    }

    /** Botão 2 — Mecânica extra (Semana 3). */
    public void acaoBotao2() {
        // TODO Semana 3
    }

    public void reiniciar() {
        inicializar();
    }

    // Getters
    public Player          getPlayer()          { return player; }
    public ObstacleManager getObstacleManager() { return obstacleManager; }
    public float           getScrollX()         { return scrollX; }
    public float           getVelocidadeAtual() { return velocidadeAtual; }
    public EstadoJogo      getEstado()          { return estado; }
    public int             getPontuacao()       { return (int) pontuacao; }
    public boolean         podeReiniciar()      { return timerMorte <= 0f; }
}