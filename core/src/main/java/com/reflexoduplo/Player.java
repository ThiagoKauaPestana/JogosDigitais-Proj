package com.reflexoduplo.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class Player {

    // Posição e tamanho
    public float x;
    public float y;
    public static final float WIDTH  = 40f;
    public static final float HEIGHT = 40f;

    // Física (preparada para Semana 2)
    private float velocityY;
    private static final float GRAVITY = -500f;

    // Linha atual do personagem (preparado para Semana 2)
    // LINHA_BAIXO = chão, LINHA_CIMA = topo
    public enum Linha { BAIXO, CIMA }
    private Linha linhaAtual = Linha.BAIXO;

    // Referências às alturas das linhas (definidas pela tela)
    private final float yLinhaBaixo;
    private final float yLinhaCima;

    // Estado
    private boolean noChao;

    public Player(float xInicial, float yLinhaBaixo, float yLinhaCima) {
        this.yLinhaBaixo = yLinhaBaixo;
        this.yLinhaCima  = yLinhaCima;
        this.x = xInicial;
        this.y = yLinhaBaixo;
        this.noChao = true;
        this.velocityY = 0f;
    }

    public void update(float delta) {
        if (!noChao) {
            velocityY += GRAVITY * delta;
            y += velocityY * delta;

            // Verifica se voltou ao chão
            if (y <= yLinhaBaixo) {
                y = yLinhaBaixo;
                velocityY = 0f;
                noChao = true;
                linhaAtual = Linha.BAIXO;
            }
        }
    }


    public void render(ShapeRenderer shapeRenderer) {
        // Corpo principal
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(x, y, WIDTH, HEIGHT);

        // Detalhe: "olhos" para indicar direção
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x + 8,  y + 22, 8, 8);
        shapeRenderer.rect(x + 24, y + 22, 8, 8);
    }


    /** Troca entre linha de cima e linha de baixo (Botão 1 - Semana 2). */
    public void trocarLinha() {
        if (linhaAtual == Linha.BAIXO) {
            y = yLinhaCima;
            linhaAtual = Linha.CIMA;
        } else {
            y = yLinhaBaixo;
            linhaAtual = Linha.BAIXO;
        }
        velocityY = 0f;
        noChao = true;
    }

    /** Retorna o retângulo de colisão do personagem (Semana 2). */
    public com.badlogic.gdx.math.Rectangle getBounds() {
        return new com.badlogic.gdx.math.Rectangle(x, y, WIDTH, HEIGHT);
    }

    public Linha getLinhaAtual() { return linhaAtual; }
    public boolean isNoChao()    { return noChao; }
}
