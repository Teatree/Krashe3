package com.mygdx.game.utils;

import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

public class CameraShaker {

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

        //screen flashes
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
            // When the shaking is over move the camera back to the center
//      GameStage.viewport.getCamera().position.x = Gdx.graphics.getWidth()/2;
//      GameStage.viewport.getCamera().position.y = Gdx.graphics.getHeight()/2 + 2;
//            blinkCounter = 0;
            ComponentRetriever.get(GameScreenScript.background, LayerMapComponent.class).getLayer("blink").isVisible = false;
            GameStage.viewport.apply(true);
        }
    }

    public void blink() {
        this.blinkIntervalCounter--;
        LayerMapComponent lc = ComponentRetriever.get(GameScreenScript.background, LayerMapComponent.class);
        if (this.blinkIntervalCounter == 0 && blinkCounter != 0) {
            lc.getLayer("blink").isVisible = !lc.getLayer("blink").isVisible;
            this.blinkIntervalCounter = blinkInterval;
            blinkCounter--;
        }
        if (blinkCounter == 0) {
            lc.getLayer("blink").isVisible = false;
        }
    }

    public void initBlinking(int blinkInterval, int amount) {
        this.blinkIntervalCounter = blinkInterval;
        this.blinkInterval = blinkInterval;
        this.blinkCounter = amount;

        LayerMapComponent lc = ComponentRetriever.get(GameScreenScript.background, LayerMapComponent.class);
        lc.getLayer("blink").isVisible = true;
    }
}