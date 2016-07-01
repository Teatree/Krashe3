package com.mygdx.etf.utils;

import com.mygdx.etf.Main;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.etf.stages.GameStage.gameScript;


public class CameraShaker {

    public static final String BLINK = "blink";

    public float time;
    Random random = new Random();
    float x, y;
    float current_time;
    float power;
    float current_power;
    int blinkIntervalCounter;
    int blinkCounter;
    int blinkInterval;

    public void initShaking(float power, float time) {
        this.power = power;
        this.time = time;
        this.current_time = 0;

        initBlinking(7, 10);
    }

    public void shake(float delta) {

        if (current_time <= time) {
            current_power = power * ((time - current_time) / time);

            x = (random.nextFloat() - 0.5f) * 2.3f * current_power;
            y = (random.nextFloat() - 0.5f) * 0.3f * current_power;
            int signX = random.nextBoolean() ? 1 : -1;
            GameStage.viewport.getCamera().translate(signX * x, y, 0);
            current_time += delta;
        } else {
            GameStage.viewport.update(Main.viewportWidth, Main.viewportHeight, true);
            ComponentRetriever.get(gameScript.background, LayerMapComponent.class).getLayer(BLINK).isVisible = false;
            Main.gameStage.getViewport().update(Main.viewportWidth, Main.viewportHeight, true);
        }

    }

    public void blink() {
        this.blinkIntervalCounter--;
        LayerMapComponent lc = ComponentRetriever.get(gameScript.background, LayerMapComponent.class);
        if (this.blinkIntervalCounter == 0 && blinkCounter != 0) {
            lc.getLayer(BLINK).isVisible = !lc.getLayer(BLINK).isVisible;
            this.blinkIntervalCounter = blinkInterval;
            blinkCounter--;
        }
        if (blinkCounter == 0) {
            lc.getLayer(BLINK).isVisible = false;
        }
    }

    public void initBlinking(int blinkInterval, int amount) {
        this.blinkIntervalCounter = blinkInterval;
        this.blinkInterval = blinkInterval;
        this.blinkCounter = amount;

        LayerMapComponent lc = ComponentRetriever.get(gameScript.background, LayerMapComponent.class);
        lc.getLayer(BLINK).isVisible = true;
    }
}