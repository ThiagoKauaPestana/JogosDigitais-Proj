package com.reflexoduplo;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameWorld {

    public static final float WORLD_WIDTH  = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    public static final float ALTURA_LINHA_BAIXO = 80f;
    public static final float ALTURA_LINHA_CIMA  = WORLD_HEIGHT - 130f - Player.HEIGHT;

    public static final float VELOCIDADE_INICIAL = 240f;
    public static final float VELOCIDADE_MAXIMA  = 520f;
    private static final float ACELERACAO        = 12f;

    private float velocidadeAtual;
    private float scrollX;
    private float tempoJogo;

    private Player          player;
    private ObstacleManager obstacleManager;

    public enum EstadoJogo { MENU, RODANDO, PERDEU }
    private EstadoJogo estado = EstadoJogo.MENU;

    private int   pontuacao      = 0;
    private int   recorde        = 0;
    private int   obstDesviados  = 0;
    private float timerPontuacao = 0f;
    private int   fase           = 1;

    private float timerMorte = 0f;
    private static final float DELAY_MORTE = 0.5f;

    public GameWorld() {
        inicializar();
    }

    private void inicializar() {
        velocidadeAtual = VELOCIDADE_INICIAL;
        scrollX         = 0f;
        tempoJogo       = 0f;
        pontuacao       = 0;
        obstDesviados   = 0;
        timerPontuacao  = 0f;
        timerMorte      = 0f;
        fase            = 1;
        estado          = EstadoJogo.MENU;

        player = new Player(160f, ALTURA_LINHA_BAIXO, ALTURA_LINHA_CIMA);
        obstacleManager = new ObstacleManager(ALTURA_LINHA_BAIXO, ALTURA_LINHA_CIMA, WORLD_WIDTH);
    }

    public void update(float delta) {
        if (estado != EstadoJogo.RODANDO) {
            if (estado == EstadoJogo.PERDEU) timerMorte -= delta;
            return;
        }

        tempoJogo += delta;
        scrollX   += velocidadeAtual * delta;

        if (velocidadeAtual < VELOCIDADE_MAXIMA) {
            velocidadeAtual = Math.min(VELOCIDADE_MAXIMA, velocidadeAtual + ACELERACAO * delta);
        }

        // Sistema de Fases
        if (pontuacao < 200) {
            fase = 1;
        } else if (pontuacao < 500) {
            fase = 2;
        } else if (pontuacao < 1000) {
            fase = 3;
        } else {
            fase = 4;
        }

        float progressoVelocidade = (velocidadeAtual - VELOCIDADE_INICIAL) / (VELOCIDADE_MAXIMA - VELOCIDADE_INICIAL);
        
        float intervaloBaseInicial = ObstacleManager.INTERVALO_INICIAL;
        float intervaloBaseMinimo  = ObstacleManager.INTERVALO_MINIMO;

        if (fase == 2) {
            intervaloBaseInicial *= 0.75f;
            intervaloBaseMinimo  *= 0.75f;
        } else if (fase == 3) {
            intervaloBaseInicial *= 0.55f;
            intervaloBaseMinimo  *= 0.55f;
        } else if (fase >= 4) {
            intervaloBaseInicial *= 0.40f;
            intervaloBaseMinimo  *= 0.40f;
        }

        float intervaloCalculado = intervaloBaseInicial - progressoVelocidade * (intervaloBaseInicial - intervaloBaseMinimo);
        obstacleManager.setIntervalo(intervaloCalculado);

        player.update(delta);
        
        // Lógica de pontuação por obstáculos superados (calculada com segurança pelo tamanho da lista)
        int totalAntes = obstacleManager.getObstaculos().size;
        obstacleManager.update(delta, velocidadeAtual);
        int totalDepois = obstacleManager.getObstaculos().size;

        if (totalDepois < totalAntes) {
            int removidos = totalAntes - totalDepois;
            obstDesviados += removidos;
            pontuacao     += removidos * 15;
        }

        // Pontuação passiva por tempo
        timerPontuacao += delta;
        if (timerPontuacao >= 1f) {
            timerPontuacao -= 1f;
            pontuacao += 10;
        }

        verificarColisoes();
    }

    private void verificarColisoes() {
        Rectangle bp = player.getBounds();
        for (Obstacle obs : obstacleManager.getObstaculos()) {
            if (bp.overlaps(obs.getBounds())) {
                // CORREÇÃO 1: Voltou a usar o método original que sua classe Player possui
                player.acionarFlashColisao(); 
                if (pontuacao > recorde) recorde = pontuacao;
                estado     = EstadoJogo.PERDEU;
                timerMorte = DELAY_MORTE;
                return;
            }
        }
    }

    public void acaoBotao1() {
        if (estado == EstadoJogo.MENU)    { iniciarJogo(); return; }
        if (estado == EstadoJogo.RODANDO) { player.trocarLinha(); }
        if (estado == EstadoJogo.PERDEU && podeReiniciar()) { reiniciar(); }
    }

    public void acaoBotao2() {
        if (estado == EstadoJogo.MENU)    { iniciarJogo(); return; }
        if (estado == EstadoJogo.PERDEU && podeReiniciar()) { reiniciar(); }
    }

    private void iniciarJogo() {
        int recordeTemp = recorde;
        inicializar();
        recorde = recordeTemp;
        estado  = EstadoJogo.RODANDO;
    }

    public void reiniciar() {
        int recordeTemp = recorde;
        inicializar();
        recorde = recordeTemp;
        estado  = EstadoJogo.RODANDO;
    }

    // Getters
    public Player          getPlayer()          { return player; }
    public ObstacleManager getObstacleManager() { return obstacleManager; }
    public float           getScrollX()         { return scrollX; }
    public float           getVelocidadeAtual() { return velocidadeAtual; }
    public EstadoJogo      getEstado()          { return estado; }
    public int             getPontuacao()       { return pontuacao; }
    public int             getRecorde()         { return recorde; }
    public float           getTempoJogo()       { return tempoJogo; }
    public boolean         podeReiniciar()      { return timerMorte <= 0f; }
    public int             getFase()            { return fase; }
}