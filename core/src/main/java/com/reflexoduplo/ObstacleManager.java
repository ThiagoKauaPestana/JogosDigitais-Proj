package com.reflexoduplo;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class ObstacleManager {

    private final Array<Obstacle> obstaculos = new Array<>();

    private float timerSpawn = 0f;
    private float intervaloSpawn;
    private static final float INTERVALO_INICIAL = 2.2f;
    private static final float INTERVALO_MINIMO  = 0.8f;

    private static final float OBS_LARGURA = 35f;
    private static final float OBS_ALTURA  = 50f;

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
        // Atualiza e remove obstáculos que saíram da tela
        for (int i = obstaculos.size - 1; i >= 0; i--) {
            Obstacle obs = obstaculos.get(i);
            obs.update(delta, velocidade);
            if (obs.isMorto()) obstaculos.removeIndex(i);
        }

        // Timer de spawn
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
            worldWidth + 10f,
            yObs,
            OBS_LARGURA,
            OBS_ALTURA,
            tipo
        ));
    }

    /** Chamado pela progressão de dificuldade na Semana 3. */
    public void aumentarDificuldade(float novoIntervalo) {
        intervaloSpawn = Math.max(INTERVALO_MINIMO, novoIntervalo);
    }

    public void reiniciar() {
        obstaculos.clear();
        timerSpawn     = 0f;
        intervaloSpawn = INTERVALO_INICIAL;
    }

    public Array<Obstacle> getObstaculos() { return obstaculos; }
}