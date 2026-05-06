package com.reflexoduplo.world;

import com.reflexoduplo.entities.Player;


public class GameWorld {

    // Dimensões lógicas do mundo (independentes da resolução de tela)
    public static final float WORLD_WIDTH  = 800f;
    public static final float WORLD_HEIGHT = 480f;

    // Alturas das duas linhas de jogo
    public static final float ALTURA_LINHA_BAIXO = 60f;
    public static final float ALTURA_LINHA_CIMA  = WORLD_HEIGHT - 60f - Player.HEIGHT;

    // Velocidade inicial do scroll (aumentará na Semana 3 - progressão de dificuldade)
    public static final float VELOCIDADE_INICIAL = 250f;
    private float velocidadeAtual;

    // Scroll acumulado (usado pelo Renderer para posicionar o fundo)
    private float scrollX;

    // Entidades
    private Player player;

    // Estado do jogo
    public enum EstadoJogo { RODANDO, PERDEU }
    private EstadoJogo estado;

    // Pontuação (placeholder Semana 3)
    private float pontuacao;

    public GameWorld() {
        velocidadeAtual = VELOCIDADE_INICIAL;
        scrollX = 0f;
        estado  = EstadoJogo.RODANDO;
        pontuacao = 0f;

        player = new Player(
            100f,               // posição X fixa do personagem
            ALTURA_LINHA_BAIXO, // Y da linha de baixo
            ALTURA_LINHA_CIMA   // Y da linha de cima
        );
    }

    /**
     * Game loop principal - chamado a cada frame pelo GameScreen.
     */
    public void update(float delta) {
        if (estado != EstadoJogo.RODANDO) return;

        // Move o mundo (runner: personagem fica parado, cenário avança)
        scrollX += velocidadeAtual * delta;

        // Atualiza o personagem
        player.update(delta);

        // Acumula pontuação por tempo sobrevivido (Semana 3 expandirá isso)
        pontuacao += velocidadeAtual * delta * 0.1f;

    }


    /** Botão 1 — Trocar de linha (implementação completa na Semana 2). */
    public void acaoBotao1() {
        if (estado == EstadoJogo.RODANDO) {
            player.trocarLinha();
        }
    }

    /** Botão 2 — Variação da mecânica (Semana 2). */
    public void acaoBotao2() {
        // TODO Semana 2
    }

    /** Reinicia o jogo (botão Retry - Semana 4). */
    public void reiniciar() {
        scrollX = 0f;
        pontuacao = 0f;
        velocidadeAtual = VELOCIDADE_INICIAL;
        estado = EstadoJogo.RODANDO;
        player = new Player(100f, ALTURA_LINHA_BAIXO, ALTURA_LINHA_CIMA);
    }

    // Getters
    public Player    getPlayer()          { return player; }
    public float     getScrollX()         { return scrollX; }
    public float     getVelocidadeAtual() { return velocidadeAtual; }
    public EstadoJogo getEstado()         { return estado; }
    public int       getPontuacao()       { return (int) pontuacao; }

    // Setter usado pela colisão (Semana 2)
    public void setEstado(EstadoJogo estado) { this.estado = estado; }
}
