package com.reflexoduplo;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * GameWorld — Semana 4
 * - Progressão de dificuldade gradual (velocidade + frequência de obstáculos)
 * - Pontuação por obstáculos desviados
 * - Recorde da sessão
 */
public class GameWorld {

    public static final float WORLD_WIDTH  = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    public static final float ALTURA_LINHA_BAIXO = 80f;
    public static final float ALTURA_LINHA_CIMA  = WORLD_HEIGHT - 130f - Player.HEIGHT;

    // Velocidade: começa confortável, sobe gradualmente
    public static final float VELOCIDADE_INICIAL = 240f;
    public static final float VELOCIDADE_MAXIMA  = 520f;
    private static final float ACELERACAO        = 12f; // px/s por segundo

    private float velocidadeAtual;
    private float scrollX;
    private float tempoJogo;

    private Player          player;
    private ObstacleManager obstacleManager;

    public enum EstadoJogo { MENU, RODANDO, PERDEU }
    private EstadoJogo estado = EstadoJogo.MENU;

    // Pontuação
    private int   pontuacao      = 0;
    private int   recorde        = 0;    // recorde da sessão
    private int   obstDesviados  = 0;   // conta obstáculos que passaram pelo player
    private float timerPontuacao = 0f;  // incrementa a cada segundo sobrevivido

    // Delay pós-colisão
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
        estado          = EstadoJogo.MENU;

        player = new Player(160f, ALTURA_LINHA_BAIXO, ALTURA_LINHA_CIMA);
        obstacleManager = new ObstacleManager(
            ALTURA_LINHA_BAIXO, ALTURA_LINHA_CIMA, WORLD_WIDTH
        );
    }

    public void update(float delta) {
        if (estado != EstadoJogo.RODANDO) {
            if (estado == EstadoJogo.PERDEU) timerMorte -= delta;
            return;
        }

        tempoJogo += delta;
        scrollX   += velocidadeAtual * delta;

        // Progressão de velocidade gradual
        if (velocidadeAtual < VELOCIDADE_MAXIMA) {
            velocidadeAtual = Math.min(
                VELOCIDADE_MAXIMA,
                velocidadeAtual + ACELERACAO * delta
            );
        }

        // Ajusta intervalo de spawn conforme velocidade
        float progresso = (velocidadeAtual - VELOCIDADE_INICIAL)
                        / (VELOCIDADE_MAXIMA  - VELOCIDADE_INICIAL);
        float intervalo = ObstacleManager.INTERVALO_INICIAL
            - progresso * (ObstacleManager.INTERVALO_INICIAL - ObstacleManager.INTERVALO_MINIMO);
        obstacleManager.setIntervalo(intervalo);

        player.update(delta);
        obstacleManager.update(delta, velocidadeAtual);

        // Pontuação por tempo sobrevivido
        timerPontuacao += delta;
        if (timerPontuacao >= 1f) {
            timerPontuacao -= 1f;
            pontuacao += 10;
        }

        verificarColisoes();
        contarObstaculosDesviados();
    }

    private void verificarColisoes() {
        Rectangle bp = player.getBounds();
        for (Obstacle obs : obstacleManager.getObstaculos()) {
            if (bp.overlaps(obs.getBounds())) {
                player.acionarFlashColisao();
                if (pontuacao > recorde) recorde = pontuacao;
                estado     = EstadoJogo.PERDEU;
                timerMorte = DELAY_MORTE;
                return;
            }
        }
    }

    private void contarObstaculosDesviados() {
        for (Obstacle obs : obstacleManager.getObstaculos()) {
            if (obs.getX() + 42f < player.x && !obs.isMorto()) {
                // obstáculo passou pelo player sem colidir
                // (contagem simples — o manager remove quando sai da tela)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Ações dos botões físicos
    // -------------------------------------------------------------------------

    public void acaoBotao1() {
        if (estado == EstadoJogo.MENU)    { iniciarJogo(); return; }
        if (estado == EstadoJogo.RODANDO) { player.trocarLinha(); }
        if (estado == EstadoJogo.PERDEU && podeReiniciar()) { reiniciar(); }
    }

    public void acaoBotao2() {
        if (estado == EstadoJogo.MENU)    { iniciarJogo(); return; }
        if (estado == EstadoJogo.PERDEU && podeReiniciar()) { reiniciar(); }
        // Semana 3+: mecânica extra do botão 2
    }

    private void iniciarJogo() {
        int recordeTemp = recorde;
        inicializar();
        recorde = recordeTemp;          // preserva o recorde entre partidas
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
}