package com.reflexoduplo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {

    public enum Tipo { BAIXO, CIMA }

    private float x;
    private float y;
    private final float largura;
    private final float altura;
    private final Tipo  tipo;
    private boolean morto = false;

    private static final Color COR_BAIXO = new Color(1f, 0.3f, 0.2f, 1f);
    private static final Color COR_CIMA  = new Color(1f, 0.6f, 0.1f, 1f);

    public Obstacle(float xInicial, float yInicial, float largura, float altura, Tipo tipo) {
        this.x       = xInicial;
        this.y       = yInicial;
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
        sr.setColor(cor.r * 0.3f, cor.g * 0.3f, cor.b * 0.3f, 0.6f);
        sr.rect(x - 2, y - 2, largura + 4, altura + 4);

        // Corpo
        sr.setColor(cor);
        sr.rect(x, y, largura, altura);

        // Listras de aviso
        sr.setColor(1f, 1f, 0f, 0.25f);
        for (float ox = 0; ox < largura; ox += 14f) {
            float x1 = x + ox;
            float x2 = Math.min(x1 + 7f, x + largura);
            sr.rect(x1, y, x2 - x1, altura);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 3, y + 3, largura - 6, altura - 6);
    }

    public boolean isMorto() { return morto; }
    public float   getX()    { return x; }
    public Tipo    getTipo() { return tipo; }
}