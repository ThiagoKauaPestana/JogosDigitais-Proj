package com.reflexoduplo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    public float x;
    public float y;
    public static final float WIDTH  = 52f;
    public static final float HEIGHT = 52f;

    private final float yLinhaBaixo;
    private final float yLinhaCima;

    public enum Linha { BAIXO, CIMA }
    private Linha linhaAtual = Linha.BAIXO;

    private float yAtual;
    private float yAlvo;
    private static final float VEL_TWEEN = 1100f;

    private float tempoFlashColisao = 0f;
    private static final float DUR_FLASH = 0.4f;


    private float pulso = 0f;

    public Player(float xInicial, float yLinhaBaixo, float yLinhaCima) {
        this.yLinhaBaixo = yLinhaBaixo;
        this.yLinhaCima  = yLinhaCima;
        this.x      = xInicial;
        this.y      = yLinhaBaixo;
        this.yAtual = yLinhaBaixo;
        this.yAlvo  = yLinhaBaixo;
    }

    public void update(float delta) {
        if (Math.abs(yAtual - yAlvo) > 1f) {
            float dir = (yAlvo > yAtual) ? 1f : -1f;
            yAtual += dir * VEL_TWEEN * delta;
            if (dir > 0 && yAtual > yAlvo) yAtual = yAlvo;
            if (dir < 0 && yAtual < yAlvo) yAtual = yAlvo;
        } else {
            yAtual = yAlvo;
        }
        y = yAtual;

        pulso += delta * 3f;
        if (tempoFlashColisao > 0) tempoFlashColisao -= delta;
    }

    public void render(ShapeRenderer sr) {
        float brilho = 0.85f + 0.15f * (float) Math.sin(pulso);

        Color cor;
        if (tempoFlashColisao > 0) {
            float t = tempoFlashColisao / DUR_FLASH;
            cor = new Color(1f, 1f - t, 1f - t, 1f);
        } else {
            cor = (linhaAtual == Linha.CIMA)
                ? new Color(1f, 0.9f * brilho, 0f, 1f)      // amarelo vibrante
                : new Color(0f, 0.85f * brilho, 1f, 1f);    // ciano vibrante
        }

        // Halo externo (acessibilidade: borda bem visível)
        sr.setColor(cor.r * 0.4f, cor.g * 0.4f, cor.b * 0.4f, 0.6f);
        sr.rect(x - 5, y - 5, WIDTH + 10, HEIGHT + 10);

        // Corpo
        sr.setColor(cor);
        sr.rect(x, y, WIDTH, HEIGHT);

        // Detalhe interno escuro
        sr.setColor(0.05f, 0.05f, 0.12f, 1f);
        sr.rect(x + 10, y + 28, 10, 10);
        sr.rect(x + 32, y + 28, 10, 10);

        // Seta de direção clara para indicar qual linha
        sr.setColor(Color.WHITE);
        if (linhaAtual == Linha.BAIXO) {
            sr.triangle(x + WIDTH / 2f, y - 10,
                        x + WIDTH / 2f - 8, y,
                        x + WIDTH / 2f + 8, y);
        } else {
            sr.triangle(x + WIDTH / 2f, y + HEIGHT + 10,
                        x + WIDTH / 2f - 8, y + HEIGHT,
                        x + WIDTH / 2f + 8, y + HEIGHT);
        }
    }

    public void trocarLinha() {
        if (linhaAtual == Linha.BAIXO) {
            linhaAtual = Linha.CIMA;
            yAlvo = yLinhaCima;
        } else {
            linhaAtual = Linha.BAIXO;
            yAlvo = yLinhaBaixo;
        }
    }

    public void acionarFlashColisao() {
        tempoFlashColisao = DUR_FLASH;
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 5, y + 5, WIDTH - 10, HEIGHT - 10);
    }

    public Linha getLinhaAtual()  { return linhaAtual; }
    public float getYLinhaBaixo() { return yLinhaBaixo; }
    public float getYLinhaCima()  { return yLinhaCima; }
}