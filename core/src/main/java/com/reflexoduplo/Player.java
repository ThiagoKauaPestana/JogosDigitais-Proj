package com.reflexoduplo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    public float x;
    public float y;
    public static final float WIDTH  = 40f;
    public static final float HEIGHT = 40f;

    private final float yLinhaBaixo;
    private final float yLinhaCima;

    public enum Linha { BAIXO, CIMA }
    private Linha linhaAtual = Linha.BAIXO;

    // Animação suave de troca de linha (tweening)
    private float yAtual;
    private float yAlvo;
    private static final float VEL_TWEEN = 900f;

    // Timers de feedback visual
    private float tempoFlashColisao = 0f;
    private static final float DUR_FLASH = 0.3f;

    public Player(float xInicial, float yLinhaBaixo, float yLinhaCima) {
        this.yLinhaBaixo = yLinhaBaixo;
        this.yLinhaCima  = yLinhaCima;
        this.x      = xInicial;
        this.y      = yLinhaBaixo;
        this.yAtual = yLinhaBaixo;
        this.yAlvo  = yLinhaBaixo;
    }

    public void update(float delta) {
        // Interpolação suave até a linha alvo
        if (Math.abs(yAtual - yAlvo) > 1f) {
            float dir = (yAlvo > yAtual) ? 1f : -1f;
            yAtual += dir * VEL_TWEEN * delta;
            if (dir > 0 && yAtual > yAlvo) yAtual = yAlvo;
            if (dir < 0 && yAtual < yAlvo) yAtual = yAlvo;
        } else {
            yAtual = yAlvo;
        }
        y = yAtual;

        if (tempoFlashColisao > 0) tempoFlashColisao -= delta;
    }

    public void render(ShapeRenderer sr) {
        // Cor: flash de colisão tem prioridade, depois cor por linha
        Color cor;
        if (tempoFlashColisao > 0) {
            cor = Color.RED;
        } else {
            cor = (linhaAtual == Linha.CIMA) ? Color.YELLOW : Color.CYAN;
        }

        // Sombra/glow
        sr.setColor(cor.r * 0.3f, cor.g * 0.3f, cor.b * 0.3f, 0.5f);
        sr.rect(x - 3, y - 3, WIDTH + 6, HEIGHT + 6);

        // Corpo principal
        sr.setColor(cor);
        sr.rect(x, y, WIDTH, HEIGHT);

        // Olhos
        sr.setColor(0.05f, 0.05f, 0.15f, 1f);
        sr.rect(x + 8,  y + 22, 8, 8);
        sr.rect(x + 24, y + 22, 8, 8);

        // Seta indicando a linha atual
        sr.setColor(Color.WHITE);
        if (linhaAtual == Linha.BAIXO) {
            sr.triangle(x + WIDTH / 2f, y - 8,
                        x + WIDTH / 2f - 6, y,
                        x + WIDTH / 2f + 6, y);
        } else {
            sr.triangle(x + WIDTH / 2f, y + HEIGHT + 8,
                        x + WIDTH / 2f - 6, y + HEIGHT,
                        x + WIDTH / 2f + 6, y + HEIGHT);
        }
    }

    /** Botão 1 — Troca entre linha de baixo e linha de cima. */
    public void trocarLinha() {
        if (linhaAtual == Linha.BAIXO) {
            linhaAtual = Linha.CIMA;
            yAlvo = yLinhaCima;
        } else {
            linhaAtual = Linha.BAIXO;
            yAlvo = yLinhaBaixo;
        }
    }

    /** Acionado pela colisão — pisca vermelho. */
    public void acionarFlashColisao() {
        tempoFlashColisao = DUR_FLASH;
    }

    /** Hitbox ligeiramente menor que o sprite (mais justo para o jogador). */
    public Rectangle getBounds() {
        return new Rectangle(x + 4, y + 4, WIDTH - 8, HEIGHT - 8);
    }

    public Linha getLinhaAtual()  { return linhaAtual; }
    public float getYLinhaBaixo() { return yLinhaBaixo; }
    public float getYLinhaCima()  { return yLinhaCima; }
}