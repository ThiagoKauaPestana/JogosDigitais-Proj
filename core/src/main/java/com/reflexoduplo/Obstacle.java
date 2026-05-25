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

    // Cores fortes e contrastantes mantidas do original
    private static final Color COR_BAIXO       = new Color(0.95f, 0.2f,  0.1f,  1f);
    private static final Color COR_CIMA        = new Color(0.95f, 0.55f, 0.05f, 1f);

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

        // 1. Desenha a Sombra do Espinho (Triângulo deslocado e escurecido)
        sr.setColor(cor.r * 0.2f, cor.g * 0.2f, cor.b * 0.2f, 0.6f);
        if (tipo == Tipo.BAIXO) {
            sr.triangle(x - 3, y, x + largura + 3, y, x + largura / 2f, y + altura + 4);
        } else {
            sr.triangle(x - 3, y + altura, x + largura + 3, y + altura, x + largura / 2f, y - 4);
        }

        // 2. Corpo Principal do Espinho (Triângulo)
        sr.setColor(cor);
        if (tipo == Tipo.BAIXO) {
            sr.triangle(x, y, x + largura, y, x + largura / 2f, y + altura);
        } else {
            sr.triangle(x, y + altura, x + largura, y + altura, x + largura / 2f, y);
        }

        // 3. Linha vertical de brilho central para dar tridimensionalidade
        sr.setColor(1f, 1f, 1f, 0.35f);
        if (tipo == Tipo.BAIXO) {
            sr.line(x + largura / 2f, y + altura, x + largura / 2f, y);
        } else {
            sr.line(x + largura / 2f, y, x + largura / 2f, y + altura);
        }
    }

    public Rectangle getBounds() {
        // Recorte nas laterais para que o jogador não colida "no vento" perto da ponta do triângulo
        return new Rectangle(x + 7, y + 2, largura - 14, altura - 6);
    }

    public boolean isMorto() { return morto; }
    public float getX() { return x; }
    public Tipo getTipo() { return tipo; }
}