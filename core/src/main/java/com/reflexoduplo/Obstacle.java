package com.reflexoduplo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Obstacle — Semana 4
 * - Visual mais claro e contrastante para facilitar leitura pelo público idoso
 */
public class Obstacle {

    public enum Tipo { BAIXO, CIMA }

    private float x;
    private float y;
    private final float largura;
    private final float altura;
    private final Tipo  tipo;
    private boolean morto = false;

    // Cores fortes e contrastantes
    private static final Color COR_BAIXO       = new Color(0.95f, 0.2f,  0.1f,  1f);
    private static final Color COR_CIMA        = new Color(0.95f, 0.55f, 0.05f, 1f);
    private static final Color COR_AVISO       = new Color(1f,    1f,    0.1f,  0.35f);

    public Obstacle(float x, float y, float largura, float altura, Tipo tipo) {
        this.x       = x;
        this.y       = y;
        this.largura = largura;
        this.altura  = altura;
        this.tipo    = tipo;
    }

    public void update(float delta, float velocidade) {
        x -= velocidade * delta;
        if (x + largura < 0) morto = true;
    }

    public void render(ShapeRenderer sr) {
        Color cor = (tipo == Tipo.BAIXO) ? COR_BAIXO : COR_CIMA;

        // Sombra
        sr.setColor(cor.r * 0.25f, cor.g * 0.25f, cor.b * 0.25f, 0.7f);
        sr.rect(x - 3, y - 3, largura + 6, altura + 6);

        // Corpo
        sr.setColor(cor);
        sr.rect(x, y, largura, altura);

        // Listras de aviso
        sr.setColor(COR_AVISO);
        for (float ox = 0; ox < largura; ox += 16f) {
            float x1 = x + ox;
            float x2 = Math.min(x1 + 8f, x + largura);
            sr.rect(x1, y, x2 - x1, altura);
        }

        // Borda clara para destacar contra o fundo
        sr.setColor(1f, 1f, 1f, 0.25f);
        sr.rect(x, y + altura - 3, largura, 3);
        sr.rect(x, y, largura, 3);
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 4, y + 4, largura - 8, altura - 8);
    }

    public boolean isMorto() { return morto; }
    public float   getX()    { return x; }
    public Tipo    getTipo() { return tipo; }
}