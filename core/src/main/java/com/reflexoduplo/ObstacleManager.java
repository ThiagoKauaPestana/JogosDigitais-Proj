package com.reflexoduplo;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * ObstacleManager — Semana 4
 * - Progressão de dificuldade gradual (velocidade e intervalo)
 * - Começa lento para não frustrar o jogador idoso
 */
public class ObstacleManager {

    private final Array<Obstacle> obstaculos = new Array<>();

    private float timerSpawn    = 0f;
    private float intervaloSpawn;

    // Dificuldade começa bem generosa para o público idoso
    public static final float INTERVALO_INICIAL = 3.0f;
    public static final float INTERVALO_MINIMO  = 1.1f;

    private static final float OBS_LARGURA = 42f;
    private static final float OBS_ALTURA  = 58f;

    private final float yLinhaBaixo;
    private final float yLinhaCima;
    private final float worldWidth;

    public ObstacleManager(float yLinhaBaixo, float yLinhaCima, float worldWidth) {
        this.yLinhaBaixo    = yLinhaBaixo;
        this.yLinhaCima     = yLinhaCima;
        this.worldWidth     = worldWidth;
        this.intervaloSpawn = INTERVALO_INICIAL;
    }

    public void update(float delta, float velocidade) {
        for (int i = obstaculos.size - 1; i >= 0; i--) {
            Obstacle obs = obstaculos.get(i);
            obs.update(delta, velocidade);
            if (obs.isMorto()) obstaculos.removeIndex(i);
        }

        timerSpawn += delta;
        if (timerSpawn >= intervaloSpawn) {
            timerSpawn = 0f;
            spawnObstaculo();
        }
    }

    private void spawnObstaculo() {
        boolean bloquearBaixo = MathUtils.randomBoolean();
        Obstacle.Tipo tipo = bloquearBaixo ? Obstacle.Tipo.BAIXO : Obstacle.Tipo.CIMA;
        float yObs = bloquearBaixo ? yLinhaBaixo : yLinhaCima;

        obstaculos.add(new Obstacle(
            worldWidth + 10f, yObs,
            OBS_LARGURA, OBS_ALTURA, tipo
        ));
    }

    /** Chamado pelo GameWorld a cada frame para ajustar dificuldade. */
    public void setIntervalo(float intervalo) {
        this.intervaloSpawn = Math.max(INTERVALO_MINIMO, intervalo);
    }

    public void reiniciar() {
        obstaculos.clear();
        timerSpawn     = 0f;
        intervaloSpawn = INTERVALO_INICIAL;
    }

    public Array<Obstacle> getObstaculos() { return obstaculos; }
}